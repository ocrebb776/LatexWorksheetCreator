package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

public class Section extends LaTexNode {
        public Section() {
            super("\\section", "","Section");
            setQuantity("Section Name","Section Name","text");
        }


    @Override
    public String customString(Project project) {
        return "{"+getQuantityValue("Section Name")+"}";
    }
    @Override
    public String summaryText() {
        String v =getQuantityValue("Section Name");
        if(v.isEmpty()) return "";
        return " - "+v;
    }

}
