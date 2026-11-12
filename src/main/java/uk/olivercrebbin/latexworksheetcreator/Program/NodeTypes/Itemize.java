package uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes;

import uk.olivercrebbin.latexworksheetcreator.Program.LaTexNode;

public class Itemize extends LaTexNode {
    public Itemize() {
        super("\\begin{itemize}", "\\end{itemize}","Itemize");
    }
}
