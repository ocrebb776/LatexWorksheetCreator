package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;

public class Enumerate extends LaTexNode {

    public Enumerate() {
        super("\\begin{enumerate}", "\\end{enumerate}","Enumerate");
    }


}
