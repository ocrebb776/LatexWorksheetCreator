package uk.olivercrebbin.latexworksheetcreator.Program;

import uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.*;

public class TestingClass {
    public static void main(String[] args) throws Exception {
        Project p = new Project("/Users/olivercrebbin/Desktop/LATEXTEMPLAT/testFile/saveFile.ltxws","/Users/olivercrebbin/Desktop/LATEXTEMPLAT/Main.tex");
        p.getTemplate().setArgument("TITLE","PAGE TITLE");
        Section l = new Section();
        l.setQuantityValue("Section Name","Helo");
        p.getDocHome().addChild(l);

        p.setSaveExportPath("/Users/olivercrebbin/Desktop/LATEXTEMPLAT/testFile/Original.tex");
        p.writeToFile();
        p.saveToXml();

// Load project from XML
        Project loadedProject = Project.loadFromXml("/Users/olivercrebbin/Desktop/LATEXTEMPLAT/testFile/saveFile.ltxws");
        loadedProject.setFilePath("/Users/olivercrebbin/Desktop/LATEXTEMPLAT/testFile/loadFile.ltxws");
        loadedProject.setSaveExportPath("/Users/olivercrebbin/Desktop/LATEXTEMPLAT/testFile/Copy.tex");
        loadedProject.saveToXml();
       loadedProject.writeToFile();
        int x;
    }
}
