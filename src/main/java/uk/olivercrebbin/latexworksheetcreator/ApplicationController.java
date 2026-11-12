package uk.olivercrebbin.latexworksheetcreator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.olivercrebbin.latexworksheetcreator.GUIController.GUI_Controller;
import uk.olivercrebbin.latexworksheetcreator.GUIController.View;
import uk.olivercrebbin.latexworksheetcreator.Program.Project;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationController extends Application {
    Stage curStage;
    public boolean edit = false;
    public Stage getCurStage() {
        return curStage;
    }
    private static ApplicationController instance;
    public static final List<File> pendingFiles = new ArrayList<>();

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        synchronized (pendingFiles) {
            if (!pendingFiles.isEmpty()) {
                handleFiles(new ArrayList<>(pendingFiles));
                pendingFiles.clear();
            }
        }


        curStage = stage;
        var scene = new Scene(new Pane());
        instance = this;

        GUI_Controller.setScene(scene);
        GUI_Controller.setApplicationController(this);
        GUI_Controller.switchTo(View.LAUNCH);

        stage.setScene(scene);
        stage.show();



    }
    private void handleFiles(List<File> files) {
        String names = files.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining("\n"));


        GUI_Controller.Alerts.createErrorAlert("FILE Loaded"+names,"Close");
    }

    public static URL getResource(String filePath){
        return ApplicationController.class.getResource(filePath);
    }
    public Project currProject;
    public VBox mainContentVBox;
    public void newFile(String validatedFileName,String template){
        currProject = new Project(validatedFileName,template);
        Controler();
    }
    public void openFile(String validatedFileName) throws Exception {
        currProject = Project.loadFromXml(validatedFileName);
        Controler();
        updateWholeScreen();
    }
    public void Controler(){

        //the program should allow the user to edit the template fill in sections

        // then List all Lodes below
        // --- 1. Root and Layout Setup ---
        BorderPane root = new BorderPane();
        root.setPrefSize(700.0, 700.0);

        // --- 2. Top Region: MenuBar ---
        MenuBar menuBar = GUI_Controller.createMenuBar();
        // Ensure menu bar uses system menu bar
        menuBar.setUseSystemMenuBar(true);
        root.setTop(menuBar);

        // --- 3. Center Region: ScrollPane ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        // --- 4. Main Content VBox ---
        mainContentVBox = new VBox();
        mainContentVBox.setPadding(new Insets(16));
        mainContentVBox.setMaxSize(Double.MAX_VALUE, 200.0);
        mainContentVBox.setPrefWidth(Double.MAX_VALUE);
        mainContentVBox.setAlignment(Pos.CENTER);

        // Child 1: The Styled Item Container
        //VBox itemContainer = createItemContainer();

        // Child 2: The "+" Button HBox
//        HBox addButtonContainer = GUI_Controller.createAddButtonContainer(currProject.getDocHome(),mainContentVBox);
        GUI_Controller.updateElement(currProject.getDocHome(),mainContentVBox);

        // Assemble Main Content
//        mainContentVBox.getChildren().addAll( addButtonContainer);
        scrollPane.setContent(mainContentVBox);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root);
        curStage.setTitle("LaTex Worksheet Creator");
        curStage.setScene(scene);
        curStage.show();
    }
    public void updateWholeScreen(){
        GUI_Controller.updateElement(currProject.getDocHome(),mainContentVBox);

    }
    public void updateTitle(){
        curStage.setTitle("LaTex Worksheet Creator"+((edit) ? "*":""));
    }

    public static void open(String file) throws Exception {
        GUI_Controller.getApplicationController().openFile(file);

    }
}