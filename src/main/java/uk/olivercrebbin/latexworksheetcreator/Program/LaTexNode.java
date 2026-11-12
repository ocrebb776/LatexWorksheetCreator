package uk.olivercrebbin.latexworksheetcreator.Program;

import java.util.*;

public class LaTexNode {
    protected  String startCommand;
    protected String endCommand;
    public List<LaTexNode> children;
    private Map<String,CustomField> attributes;
    protected String nodeType;
    private LaTexNode parent;

    public LaTexNode(String startCommand, String endCommand,String nodeType) {
        this.startCommand = startCommand;
        this.endCommand = endCommand;
        children = new ArrayList<>();
        attributes = new HashMap<>();
        this.nodeType = nodeType;
    }

    public List<LaTexNode> getChildren() {
        return children;
    }

    public LaTexNode getParent() {
        return parent;
    }

    public void setParent(LaTexNode parent) {
        this.parent = parent;
    }

    public void setChildren(List<LaTexNode> children) {
        this.children = children;
    }

    public void addChild(LaTexNode n){
        n.setParent(this);
        children.add(n);

    }
    public String customString(Project project){
        return "";
    }

    public String toString(Project project) {
        StringBuilder s = new StringBuilder();
        s.append(startCommand);
        s.append(customString(project));
        LaTexNode last = null;
        for(LaTexNode n:children){
            if(last != null && last.getNodeType().equals( "Text") && n.getNodeType().equals("Text")){
                s.append("\\blank{0.0000001px}"+ "\\\\");
            }
            s.append(n.toString(project) + "\n");
            last = n;
        }
        s.append(endCommand);
        return s.toString();
    }

    public List<String> getListOfQuantities(){
        return attributes.keySet().stream().toList();
    }
    public String getQuantityValue(String name){
        return getQuantity(name).getValue();
    }
    public void setQuantityValue(String name,String newValue){
        getQuantity(name).setValue(newValue);
    }
    public String getQuantityType(String name){
        return getQuantity(name).getType();
    }
    public CustomField getQuantity(String name){
        return attributes.get(name);
    }

    public void setQuantity(String name,String startVal,String type){
        attributes.put(name,new CustomField(name,startVal,type));
    }
    public void setQuantity(CustomField p){
        attributes.put(p.getName(),p);
    }
    public String getNodeType(){
        return nodeType;
    }

    public String summaryText(){
        return "";
    }
    public List<LaTexNode> getAllNodes(){
        List<LaTexNode> list = new ArrayList<>();
        for(LaTexNode node:children){
            list.add(node);
            list.addAll(node.getAllNodes());
        }
        return list;
    }


}
