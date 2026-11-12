package uk.olivercrebbin.latexworksheetcreator.Program;

import uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.MainbDocument;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

public class Project {
    private String filePath;
    private String saveExportPath;
    private MainbDocument docHome;
    Template template;

    public Project(String validatedFilePath, String validatedTemplateFilePath){
        filePath = validatedFilePath;
        template = new Template(validatedTemplateFilePath);
        docHome = new MainbDocument();
        images = new HashMap<>();
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(template.getPreamble());
       s.append(docHome.toString(this));
        return  s.toString();
    }

    public Template getTemplate() {
        return template;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }



    public void setDocHome(MainbDocument docHome) {
        this.docHome = docHome;
    }

    public void setSaveExportPath(String saveExportPath) {
        this.saveExportPath = saveExportPath;
    }

    public MainbDocument getDocHome() {
        return docHome;
    }
    public String writeToFile() throws IOException {
        String tmpdir = Files.createTempDirectory("tmpDirPrefix").toFile().getAbsolutePath();
        File texFile = new File(tmpdir+"/ExportFile.tex");

        System.out.println(tmpdir);
        if(tmpdir != null){
            boolean succ = FileManagement.writeToFile(texFile.getAbsolutePath(),toString());
            for(String s:getListOfImages()){
                FileManagement.saveImageToDir(new File(tmpdir+"/"+s+".png"),getImage(s));
            }
            if(succ){
                String filePath = LatexCompiler.compileTexFile(texFile.getAbsolutePath());
                System.out.println(filePath);
                if(filePath!=null){

                  return filePath;

                }

            }
       return null;

        }
        else throw new IOException("No File Save Location");
    }

    /**
     * Saves the Project to an XML file with all nested objects
     * @throws Exception If file operations fail
     */
    public void saveToXml() throws Exception {
        ProjectXmlSerializer serializer = new ProjectXmlSerializer();
        serializer.saveProjectToXml(this, filePath);
    }

    /**
     * Loads a Project from an XML file, reconstructing all nested objects
     * @param xmlFilePath Path to the XML file
     * @return Reconstructed Project instance
     * @throws Exception If file operations or parsing fail
     */
    public static Project loadFromXml(String xmlFilePath) throws Exception {
        ProjectXmlSerializer serializer = new ProjectXmlSerializer();
        Project p = serializer.loadProjectFromXml(xmlFilePath);
        p.setFilePath(xmlFilePath);
        return p;
    }

    private Map<String,String> images;
    public String getImage(String imageName){
        return images.get(imageName);
    }
    public void setImage(String imageName,String encodedImageContent){
        images.put(imageName,encodedImageContent);
    }
    public List<String> getListOfImages(){
        return images.keySet().stream().toList();
    }
    public void removeImageIfExists(String imageName){
        images.remove(imageName);
    }



}
