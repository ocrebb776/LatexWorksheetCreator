package uk.olivercrebbin.latexworksheetcreator.GUIController;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import uk.olivercrebbin.latexworksheetcreator.ApplicationController;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutPageController implements Initializable {

    @FXML
    private MenuBar menuBar;


    @FXML
    public void close(Event event) {

        Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
        s.close();
    }

    public Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.getMenus().add(GUI_Controller.getAboutMenu());

    }
}
