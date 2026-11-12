package uk.olivercrebbin.latexworksheetcreator.Program;

import org.w3c.dom.*;

import javax.print.Doc;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;
import java.lang.reflect.Constructor;

import uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.*;

/**
 * Handles XML serialization and deserialization of Project objects
 */
public class ProjectXmlSerializer {
    
    private static final String ROOT_ELEMENT = "Project";
    private static final String TEMPLATE_ELEMENT = "Template";
    private static final String DOC_HOME_ELEMENT = "DocHome";
    private static final String NODE_ELEMENT = "Node";
    private static final String CHILDREN_ELEMENT = "Children";
    private static final String CUSTOM_ATRIBUTE = "CustomAttribute";
    private static final String IMAGELIST = "ImageList";
    private static final String IMAGE = "Image";
    /**
     * Saves a Project instance to XML file
     */
    public void saveProjectToXml(Project project, String xmlFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        // Create root element
        Element rootElement = doc.createElement(ROOT_ELEMENT);
        doc.appendChild(rootElement);
        
        // Add basic project properties
        addTextElement(doc, rootElement, "filePath", project.getFilePath());
        
        // Serialize Template
        Element templateElement = serializeTemplate(doc, project.getTemplate());
        rootElement.appendChild(templateElement);

        Element imageElement  = serialiseImages(doc,project);
        rootElement.appendChild(imageElement);
        
        // Serialize DocHome (MainbDocument)
        Element docHomeElement = serializeNode(doc, project.getDocHome());
        docHomeElement.setAttribute("elementName", DOC_HOME_ELEMENT);
        rootElement.appendChild(docHomeElement);
        
        // Write to file
        writeXmlToFile(doc, xmlFilePath);
    }
    
    /**
     * Loads a Project instance from XML file
     */
    public Project loadProjectFromXml(String xmlFilePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();
        
        Element rootElement = doc.getDocumentElement();
        
        // Extract basic properties
        String filePath = getTextContent(rootElement, "filePath");
        
        // Deserialize Template
        Element templateElement = (Element) rootElement.getElementsByTagName(TEMPLATE_ELEMENT).item(0);
        Template template = deserializeTemplate(templateElement);
        
        // Create project with dummy template path (will be overwritten)
        Project project = new Project(filePath, template.getFilePath());
        project.template = template; // Replace with deserialized template
        
        // Deserialize DocHome
        NodeList docHomeNodes = rootElement.getElementsByTagName(NODE_ELEMENT);
        if (docHomeNodes.getLength() > 0) {
            Element docHomeElement = (Element) docHomeNodes.item(0);
            MainbDocument docHome = (MainbDocument) deserializeNode(docHomeElement);
            project.setDocHome(docHome);
        }
        deSerialiseImages((Element) rootElement.getElementsByTagName(IMAGELIST).item(0),project);
        return project;
    }
    
    /**
     * Serializes Template object to XML
     */
    private Element serializeTemplate(Document doc, Template template) {
        Element templateElement = doc.createElement(TEMPLATE_ELEMENT);
        
        addTextElement(doc, templateElement, "filePath", template.getFilePath());
        

        
        // Serialize arguments
        Element argumentsElement = doc.createElement("arguments");
        Map<String, String> allArgs = template.getAllArguments();
        for (Map.Entry<String, String> entry : allArgs.entrySet()) {
            Element argElement = doc.createElement("argument");
            argElement.setAttribute("key", entry.getKey());
            argElement.setTextContent(entry.getValue());
            argumentsElement.appendChild(argElement);
        }
        templateElement.appendChild(argumentsElement);
        
        return templateElement;
    }
    private Element serialiseImages(Document doc, Project project){
        Element m = doc.createElement(IMAGELIST);
        for(String imageName:project.getListOfImages()){
            Element image = doc.createElement(IMAGE);
            addTextElement(doc,image,"Name",imageName);
            addTextElement(doc,image,"Content",project.getImage(imageName));
            m.appendChild(image);
        }
        return  m;
    }

    private void deSerialiseImages(Element ImageElement,Project project){
       NodeList childList= ImageElement.getElementsByTagName(IMAGE);
       for(int i = 0;i<childList.getLength();i++){
           Element child = (Element) childList.item(0);
           String imageName = getTextContent(child,"Name");
           String imageContent = getTextContent(child,"Content");
           project.setImage(imageName,imageContent);
       }
    }
    
    /**
     * Deserializes Template object from XML
     */
    private Template deserializeTemplate(Element templateElement) throws Exception {
        String filePath = getTextContent(templateElement, "filePath");
        Template template = new Template(filePath);
        
        // Restore arguments
        NodeList argumentsList = templateElement.getElementsByTagName("argument");
        for (int i = 0; i < argumentsList.getLength(); i++) {
            Element argElement = (Element) argumentsList.item(i);
            String key = argElement.getAttribute("key");
            String value = argElement.getTextContent();
            template.setArgument(key, value);
        }
        
        return template;
    }
    
    /**
     * Serializes Node object to XML recursively
     */
    private Element serializeNode(Document doc, LaTexNode laTexNode) {
        Element nodeElement = doc.createElement(NODE_ELEMENT);
        
        // Store class name for reconstruction
        nodeElement.setAttribute("className", laTexNode.getClass().getSimpleName());
        nodeElement.setAttribute("startCommand", laTexNode.startCommand);
        nodeElement.setAttribute("endCommand", laTexNode.endCommand);
        nodeElement.setAttribute("nodeType", laTexNode.getNodeType());

        
        // Store custom string if available
        List<String> quantites = laTexNode.getListOfQuantities();
        for(String s:quantites) {
            Element attribute = doc.createElement(CUSTOM_ATRIBUTE);
            attribute.setAttribute("name",s);
            attribute.setAttribute("value",laTexNode.getQuantityValue(s));
            attribute.setAttribute("type",laTexNode.getQuantityType(s));
            nodeElement.appendChild(attribute);
        }
        
        // Serialize children
        if (laTexNode.getChildren() != null && !laTexNode.getChildren().isEmpty()) {
            Element childrenElement = doc.createElement(CHILDREN_ELEMENT);
            for (LaTexNode child : laTexNode.getChildren()) {
                Element childElement = serializeNode(doc, child);
                childrenElement.appendChild(childElement);
            }
            nodeElement.appendChild(childrenElement);
        }
        
        return nodeElement;
    }
    
    /**
     * Deserializes Node object from XML recursively
     */
    private LaTexNode deserializeNode(Element nodeElement) throws Exception {
        String className = nodeElement.getAttribute("className");
        String startCommand = nodeElement.getAttribute("startCommand");
        String endCommand = nodeElement.getAttribute("endCommand");
        String nodeType = nodeElement.getAttribute("nodeType");


        // Create node instance using reflection
        LaTexNode laTexNode = createNodeInstance(className, startCommand, endCommand, nodeType);

        // Restore attributes - FIXED: Only look at direct children
        NodeList attributeNodes = nodeElement.getChildNodes();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Node child = attributeNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && CUSTOM_ATRIBUTE.equals(child.getNodeName())) {
                Element attrElement = (Element) child;
                laTexNode.setQuantity(attrElement.getAttribute("name"), attrElement.getAttribute("value"), attrElement.getAttribute("type"));
            }
        }



        // Restore children
        NodeList childrenNodes = nodeElement.getChildNodes();
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            Node child = childrenNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && CHILDREN_ELEMENT.equals(child.getNodeName())) {
                Element childrenElement = (Element) child;
                NodeList childNodes = childrenElement.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node grandChild = childNodes.item(j);
                    if (grandChild.getNodeType() == Node.ELEMENT_NODE && NODE_ELEMENT.equals(grandChild.getNodeName())) {
                        Element childElement = (Element) grandChild;
                        LaTexNode childLaTexNode = deserializeNode(childElement);
                        laTexNode.addChild(childLaTexNode);
                    }
                }
                break; // Only process the first CHILDREN_ELEMENT
            }
        }
        
        return laTexNode;
    }
    
    /**
     * Creates Node instances using reflection based on class name
     */
    private LaTexNode createNodeInstance(String className, String startCommand, String endCommand,String nodeType) throws Exception {
        String packageName = "uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.";

        
        try {
            Class<?> clazz = Class.forName(packageName + className);
            
            // Try constructor with no parameters first (like MainbDocument)
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                return (LaTexNode) constructor.newInstance();
            } catch (NoSuchMethodException e) {
                // Try constructor with startCommand and endCommand parameters
                try {
                    Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class);
                    return (LaTexNode) constructor.newInstance(startCommand, endCommand);
                } catch (NoSuchMethodException e2) {
                    // If no suitable constructor found, use base Node constructor
                    return new LaTexNode(startCommand, endCommand,nodeType);
                }
            }
        } catch (ClassNotFoundException e) {
            // If class not found, create base Node
            return new LaTexNode(startCommand, endCommand,nodeType);
        }
    }
    
    /**
     * Helper method to add text elements
     */
    private void addTextElement(Document doc, Element parent, String elementName, String textContent) {
        if (textContent != null) {
            Element element = doc.createElement(elementName);
            element.setTextContent(textContent);
            parent.appendChild(element);
        }
    }
    
    /**
     * Helper method to get text content
     */
    private String getTextContent(Element parent, String elementName) {
        NodeList nodeList = parent.getElementsByTagName(elementName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
    
    /**
     * Writes XML document to file
     */
    private void writeXmlToFile(Document doc, String filePath) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
