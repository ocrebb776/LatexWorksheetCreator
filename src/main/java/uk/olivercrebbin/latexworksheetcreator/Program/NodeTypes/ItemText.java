package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class ItemText extends LaTexNode {

    public ItemText() {
        super("\\item ", "","ItemText");
        setQuantity("Text","Enter Text","textBox");
    }


    @Override
    public String customString(Project project) {
        return getQuantityValue("Text");
    }
    @Override
    public String summaryText() {
        String v = getQuantityValue("Text");
        if(v.isEmpty()) return "";
        return " - "+v;
    }


}

