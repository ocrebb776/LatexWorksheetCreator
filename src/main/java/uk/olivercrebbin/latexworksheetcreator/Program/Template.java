package uk.olivercrebbin.latexworksheetcreator.Program;

import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
public class Template {
    private String filePath;
    private List<String> fileData;
    private Map<String,String> arguments;
    private List<String> argumentList;
    public List<String> getFileData() {
        return fileData;
    }

    public String getFilePath() {
        return filePath;
    }
    public Template(String filePath){
        this.filePath = filePath;
        updateFileData();
        argumentList = Util.findCustomAreas(getFileDataJoined());
        arguments = new HashMap<>();
        for(String arg:argumentList){
            arguments.put(arg,"Non Defined Argument");
        }

    }
    public void setArgument(String argument,String text){
            arguments.put(argument,text);
    }
    private String getArgument(String argument){
        if(argumentList.stream().filter(x->x.equals(argument)).toList().size() >= 1){
            return arguments.get(argument);
        }
        return "ARGUMENT_NOT_FOUND";
    }

    public List<String> getArgumentList()
    {
        return new ArrayList<>(argumentList);
    }

    private void updateFileData(){
        if(filePath != null ){
            System.out.println(filePath);
            if(filePath.split("\\.")[0].equals("DEFAULTPAGE")){
                fileData = Arrays.stream(Defaults.values()).filter(x->x.name().equals(filePath.split("\\.")[1])).findFirst().orElseThrow().getData();
            }else{
            fileData = FileManagement.readFileIntoList(filePath);}
        }
    }
    public String getFileDataJoined(){
       return fileData.stream().collect(Collectors.joining("\n"));
    }
    public String getPreamble(){
        String fileText = getFileDataJoined();
        for(String i:argumentList){
            String re = "(?s)\\[{4}" +Pattern.quote(i) + "\\]{4}";

            fileText=  fileText.replaceAll(re,getArgument(i));
        }
        return fileText;
    }
    public List<String> export(){
        return new ArrayList<>();
    }
    public String getArgumentValue(String argument) {
        return arguments.getOrDefault(argument, "Non Defined Argument");
    }

    public Map<String, String> getAllArguments() {
        return new HashMap<>(arguments);
    }
}
