package uk.olivercrebbin.latexworksheetcreator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import uk.olivercrebbin.latexworksheetcreator.GUIController.GUI_Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class LauncherController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private MenuBar menuBar;

    @FXML
    protected void newFile() {
            ApplicationController c = GUI_Controller.getApplicationController();
            GUI_Controller.createNewProjectWizard(c.getCurStage());



    }

    @FXML
    protected void LoadSave() {
           GUI_Controller.openProjectFileIntoEdditor(GUI_Controller.getApplicationController().curStage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.getMenus().add(GUI_Controller.getAboutMenu());
        menuBar.setUseSystemMenuBar(true);

    }
}
