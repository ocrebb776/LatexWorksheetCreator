module uk.olivercrebbin.latexworksheetcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.swing;


    opens uk.olivercrebbin.latexworksheetcreator to javafx.fxml;
    opens uk.olivercrebbin.latexworksheetcreator.GUIController to javafx.fxml;
    exports uk.olivercrebbin.latexworksheetcreator;
}