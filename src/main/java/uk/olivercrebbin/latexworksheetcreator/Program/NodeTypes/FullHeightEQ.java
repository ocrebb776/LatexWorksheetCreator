package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class FullHeightEQ extends LaTexNode {
    public FullHeightEQ() {
        super("\\[", "\\]", "FullHeightEQ");
        setQuantity("Content","","text");
    }
    @Override
    public String customString(Project project) {
        return getQuantityValue("Content");
    }
    @Override
    public String summaryText() {
        String v =getQuantityValue("Content");
        if(v.isEmpty()) return "";
        return " - "+v;
    }
}
