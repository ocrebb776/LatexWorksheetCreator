package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class QuestionWithLines extends LaTexNode {


    public QuestionWithLines(){
        super("\\questionLines","","QuestionWithLines");
        setQuantity("Question Text","Question Text","textBox");
        setQuantity("Marks","0","number");
        setQuantity("Number Of Lines","1","number");

    }

    @Override
    public String customString(Project project) {
        return "{" + getQuantityValue("Question Text") +
                "}{" + getQuantityValue("Marks") +
                "}{" + getQuantityValue("Number Of Lines") +
                "}";
    }
    @Override
    public String summaryText() {
        String v =getQuantityValue("Question Text");
        if(v.isEmpty()) return "";
        return " - "+v;
    }

}
