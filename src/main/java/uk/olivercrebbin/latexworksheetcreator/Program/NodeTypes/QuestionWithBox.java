package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class QuestionWithBox extends LaTexNode {

    public QuestionWithBox(){
        super("\\questionAnsBox","","QuestionWithBox");

        setQuantity("Question Text","Question Text","textBox");
        setQuantity("Marks","0","number");
        setQuantity("Height of Box","1cm","text");
    }

    @Override
    public String customString(Project project) {
        return "{" + getQuantityValue("Question Text") +
                "}{" + getQuantityValue("Marks") +
                "}{" +getQuantityValue("Height of Box") +
                "}";
    }
    @Override
    public String summaryText() {
        String v =getQuantityValue("Question Text");
        if(v.isEmpty()) return "";
        return " - "+v;
    }

}
