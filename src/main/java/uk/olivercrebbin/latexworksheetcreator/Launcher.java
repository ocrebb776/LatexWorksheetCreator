package uk.olivercrebbin.latexworksheetcreator;


public class Launcher {
    public static void main(String[] args){
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", "My Mac App");
       ApplicationController.main(args);
        // Force AWT to respect macOS menu bar and appearance BEFORE it
    }
}
