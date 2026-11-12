package uk.olivercrebbin.latexworksheetcreator.Program;

public class CustomField {
    private String name;
    private String value;
    private String type;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
    public CustomField(String name,String startVal,String type){
        this.name = name;
        value=startVal;
        this.type = type;
    }

    public String getName() {
        return name;
    }
}
