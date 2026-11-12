package uk.olivercrebbin.latexworksheetcreator.GUIController;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.ByteArrayInputStream;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.olivercrebbin.latexworksheetcreator.ApplicationController;
import uk.olivercrebbin.latexworksheetcreator.Program.*;
import uk.olivercrebbin.latexworksheetcreator.Program.NodeTypes.MainbDocument;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.image.Image;

public class GUI_Controller {

    private static Map<View, Parent> cache = new HashMap<>();

    private static Scene scene;

    public static void setScene(Scene scene) {
        GUI_Controller.scene = scene;
    }

    public static void switchTo(View view) {
        if (scene == null) {
            System.out.println("No scene was set");
            return;
        }

        try {
            Parent root;

            if (cache.containsKey(view)) {
                System.out.println("Loading from cache");

                root = cache.get(view);
            } else {
                System.out.println("Loading from FXML");
                String fileName = view.getFileName();
                URL resc =  ApplicationController.getResource(fileName);
                root = FXMLLoader.load(
                        resc
                );

                cache.put(view, root);
            }

            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ApplicationController applicationController;

    public static void setApplicationController(ApplicationController applicationControlle) {
        applicationController = applicationControlle;
    }

    public  static ApplicationController getApplicationController() {
        return applicationController;
    }
    public static MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        Menu fileMenu = new Menu("File");

        MenuItem save = new MenuItem("Save");
        save.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        save.setOnAction(x->{
            try {

                applicationController.currProject.saveToXml();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            applicationController.edit = false;
            applicationController.updateTitle();
        });
        MenuItem open = new MenuItem("open");
        open.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        open.setOnAction(x->{
            if(applicationController.edit){
                Stage dialouge = GUI_Controller.createDialogue(Modality.APPLICATION_MODAL,applicationController.getCurStage());
               VBox popup = new VBox(10);
               popup.setPadding(new Insets(20));
               popup.setAlignment(Pos.CENTER);
               popup.getChildren().add(new Text("File has not been saved, do you want to save?"));
               HBox buttons = new HBox(10);
               Button no = new Button("No do not save");
               no.setOnAction(y-> openProjectFileIntoEdditor(applicationController.getCurStage()));
               Button yes = new Button("Please save on exit");
               yes.setOnAction(y->{
                   try {
                       applicationController.currProject.saveToXml();
                       Stage newPopup = GUI_Controller.createDialogue(Modality.APPLICATION_MODAL,dialouge);
                       newPopup.setTitle("SAVED");
                       Text t= new Text("SAVED FILE");
                       VBox b = new VBox(t);
                       b.setPadding(new Insets(20));
                       newPopup.setScene(new Scene(b));
                       newPopup.show();
                       openProjectFileIntoEdditor(applicationController.getCurStage());
                       dialouge.hide();
                   } catch (Exception e) {
                       Stage newPopup = GUI_Controller.createDialogue(Modality.APPLICATION_MODAL,dialouge);
                       newPopup.setTitle("ERROR");
                       Text t= new Text("Error when saving file");
                       VBox b = new VBox(t);
                       b.setPadding(new Insets(20));
                       newPopup.setScene(new Scene(b));
                       newPopup.show();
                   }
               });
               popup.getChildren().addAll(no,yes);


                dialouge.setScene(new Scene(popup));
                dialouge.setTitle("Save now");
                dialouge.show();
            }else openProjectFileIntoEdditor(applicationController.getCurStage());
        });

        MenuItem saveAs = new MenuItem("Save-as");
        saveAs.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+S"));
        saveAs.setOnAction(x->{
            Stage s = applicationController.getCurStage();
            String fileName = saveFile(s);
            if(fileName != null){
                applicationController.currProject.setFilePath(fileName);
                try {
                    applicationController.currProject.saveToXml();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        });

        Menu exportMenu = new Menu("Export");
        MenuItem export = new MenuItem("Export to pdf viewer");
        export.setAccelerator(KeyCombination.keyCombination("F5"));
        export.setOnAction(event -> {
            try {
                String filePath =applicationController.currProject.writeToFile();
                if(filePath!=null) {
                    File htmlFile = new File(filePath);
                    Desktop.getDesktop().browse(htmlFile.toURI());
                }else{
                    Alert a = new Alert(Alert.AlertType.ERROR,"ERROR CREATING FILE");
                    a.show();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem exportFolder = new MenuItem("Export to Folder");
        exportFolder.setAccelerator(KeyCombination.keyCombination("F6"));
        exportFolder.setOnAction(event -> {
            try {
                String location = applicationController.currProject.writeToFile();
                if(location == null){
                Alert a = new Alert(Alert.AlertType.ERROR,"ERROR Exporting File",new ButtonType("Go Back"));
                a.show();}else {
                    File fileLocation = new File(location);
                    if(fileLocation.exists()) {
                        FileManagement.promptAndMovePdf(applicationController.getCurStage(), fileLocation);
                    }else {
                        (new Alert(Alert.AlertType.ERROR,"No file Generated, is LaTex in stalled on your machine",new ButtonType("Go Back"))).show();
                    }

                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR,"ERROR Exporting File",new ButtonType("OH NO"));
                a.show();
                throw new RuntimeException(e);
            }
        });
        MenuItem exportToLaTex = new MenuItem("Export to LaTex");
        exportToLaTex.setAccelerator(KeyCombination.keyCombination("Shortcut+F6"));
        exportToLaTex.setOnAction(x->{
            Stage d = createDialogue(Modality.APPLICATION_MODAL,applicationController.getCurStage());
            TextArea t = new TextArea(applicationController.currProject.toString());
            d.setScene(new Scene(t,300,300));
            d.show();
        });

        Menu aboutMenu = getAboutMenu();


        Menu imageManagmentMenu = new Menu("Images");
        MenuItem manageImages = new MenuItem("Manage Images");
        manageImages.setOnAction(x->mangeImagesWindow());
        MenuItem uploadImage = new MenuItem("Upload Images");
        uploadImage.setOnAction(x->{
            String image = convertImageFromFileSystemToString(applicationController.getCurStage());
            String name = showInputAlert("Image Upload","What name would you like");

            while (true){
                 String finalName = name;
                 if(applicationController.currProject.getListOfImages().stream().anyMatch(seacrhKey-> finalName.equals(seacrhKey)))
                     name = showInputAlert("Image Upload","That name is already in use \n please select another one");
                 else break;
            }
            applicationController.currProject.setImage(name,image);

        });


        fileMenu.getItems().addAll(save,saveAs,open);
        exportMenu.getItems().addAll(export,exportFolder,exportToLaTex);
        imageManagmentMenu.getItems().addAll(manageImages,uploadImage);
        menuBar.getMenus().addAll(fileMenu,exportMenu,aboutMenu,imageManagmentMenu);
        return menuBar;
    }
    public static Menu getAboutMenu(){
        Menu aboutMenu = new Menu("About");
        MenuItem aboutPage = new MenuItem("About the Program");
        aboutMenu.getItems().add(aboutPage);
        aboutPage.setOnAction(x->{
            Stage page = createDialogue(Modality.APPLICATION_MODAL,applicationController.getCurStage());
            System.out.println("Loading from FXML");
            String fileName = "aboutPage.fxml";
            URL resc =  ApplicationController.getResource(fileName);
            Pane root;
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                 root = fxmlLoader.load(
                        resc
                );
               // ((AboutPageController) fxmlLoader.getController()).stage = page;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            page.setScene(new Scene(root));
            page.show();
        });

        MenuItem aboutDocument = new MenuItem("About Document");
        aboutDocument.setOnAction(x->{
            Project p = applicationController.currProject;
            String fileLocation = p.getFilePath();
            String fileSize;
            String dateCreated;
            String dateModified;
            File file = new File(fileLocation);

            if (file.exists()) {
                long fileSizeLong = file.length();
                double sizeInKB = (double) fileSizeLong / 1024;
                String fileSizeStr = String.format("%.1f KB", sizeInKB);
                fileSize = fileSizeStr;

                long lastModifiedTime = file.lastModified();
                Date lastModifiedDate = new Date(lastModifiedTime);
                dateModified = lastModifiedDate.toString();

                long creationTime;
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        File oldestFile = Arrays.stream(files).min(Comparator.comparing(File::lastModified)).get();
                        creationTime = oldestFile.lastModified();
                    } else {
                        creationTime = lastModifiedTime;
                    }
                } else {
                    try {
                   creationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis();
                    } catch (IOException e) {
                        creationTime = lastModifiedTime;

                    }
                }

                Date creationDate = new Date(creationTime);
                dateCreated = creationDate.toString();
                System.out.println("Creation Time: " + creationDate);

            } else {
                System.out.println("File does not exist");
                fileSize = "Not Saved";
                dateCreated = "Not Saved";
                dateModified = "Not Saved";
            }

            Stage popup = createDialogue(Modality.APPLICATION_MODAL,applicationController.getCurStage());
            VBox root = new VBox(10);
            root.setPrefWidth(400);
            root.setAlignment(Pos.CENTER);
            Label l = new Label("Document Information");
            GridPane pane = new GridPane(6,9);
            pane.setPrefWidth(Double.MAX_VALUE);
            pane.setAlignment(Pos.CENTER);

            root.getChildren().addAll(l,pane);

            Label fileLocationLabel = new Label("file location");
            TextField fileLocationField = new TextField(fileLocation);
            fileLocationField.setEditable(false);

            Label fileSizeLabel = new Label("File Size");
            TextField fileSizeField = new TextField(fileSize);
            fileSizeField.setEditable(false);

            Label fileLastModifiedLabel = new Label("Last Modified");
            TextField fileLastModifiedField = new TextField(dateModified);
            fileLastModifiedField.setEditable(false);

            Label fileCreatedLabel = new Label("Last Created");
            TextField fileCreatedField = new TextField(dateCreated);
            fileCreatedField.setEditable(false);

            pane.add(fileLocationLabel,0,0);
            pane.add(fileLocationField,1,0);
            pane.add(fileSizeLabel,0,1);
            pane.add(fileSizeField,1,1);
            pane.add(fileLastModifiedLabel,0,2);
            pane.add(fileLastModifiedField,1,2);
            pane.add(fileCreatedLabel,0,3);
            pane.add(fileCreatedField,1,3);
            popup.setScene(new Scene(root,300,200));
            popup.show();

        });
        aboutMenu.getItems().add(aboutDocument);
        return aboutMenu;
    }
    public static VBox getNewItemDialige(LaTexNode ParentNode,Stage dialog,VBox parentWrapper){
        VBox parent = new VBox();
        parent.setAlignment(Pos.CENTER);
        parent.setSpacing(10);
        Label title = new Label("Create New Node For "+DisplayHelper.getDisplayNameOfNode(ParentNode));
        ComboBox c = new ComboBox<>();
        c.getItems().addAll(DisplayHelper.possibleChild(ParentNode));

        Button go = new Button("Create Node");
        go.setOnAction(event->{
            ParentNode.addChild(((DisplayHelper.NodeTypes) c.getValue()).getItem());
            applicationController.edit = true;
            applicationController.updateTitle();

            dialog.close();
            updateElement(ParentNode,parentWrapper);


        });


        parent.getChildren().addAll(title,c,go);
        return parent;
    }
    public static Stage createDialogue(Modality modality, Stage owner){
        final Stage dialog = new Stage();
        dialog.initModality(modality);
        dialog.initOwner(owner);

        return dialog;

    }
    public static HBox createAddButtonContainer(LaTexNode parentNode,VBox parentWrapper) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefHeight(100.0);
        hBox.setPrefWidth(200.0);

        Button plusBtn = getPlusBtn(parentNode, parentWrapper);

        hBox.getChildren().add(plusBtn);
        return hBox;
    }

    private static Button getPlusBtn(LaTexNode parentNode, VBox parentWrapper) {
        Button plusBtn = new Button("+");
       // plusBtn.setPrefWidth(240.0);
        //plusBtn.setPrefHeight(26.0);

        plusBtn.setOnAction(s->{
            Stage dialog = createDialogue(Modality.APPLICATION_MODAL,applicationController.getCurStage());
            VBox dialogVbox = getNewItemDialige(parentNode,dialog, parentWrapper);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });
        return plusBtn;
    }

    public static VBox createElement(LaTexNode node,LaTexNode parentNode){
        VBox container = new VBox();
        updateElement(node,container);
        return container;
    }
    public static void updateElement(LaTexNode node,VBox container){
        VBox innerVBox = new VBox();
        ToolBar toolBar = new ToolBar();

        if(node instanceof MainbDocument){
            container.getChildren().clear();
            innerVBox = new VBox(10);

            container.getChildren().add(innerVBox);
                Template t = applicationController.currProject.getTemplate();
                for (String s : t.getArgumentList()) {
                    Label Name = new Label(s);
                    TextField field = new TextField(t.getArgumentValue(s));
                    field.setOnKeyTyped(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {

                            t.setArgument(s,field.getText());
                            applicationController.edit = true;
                            applicationController.updateTitle();

                        }
                    });
                    VBox cont = new VBox(5);
                    cont.getChildren().addAll(Name, field);
                    innerVBox.getChildren().addAll(cont);
            }

        }else {
            // Style and Margins
            container.setStyle("-fx-border-color: #999; -fx-border-width: 1;");
            VBox.setMargin(container, new Insets(3.0));

            // Toolbar with Remove Button
            toolBar.setPrefSize(200.0, 40.0);
            Button removeBtn = new Button("X");
            Button moveUp = new Button("↑");
            Button moveDown = new Button("↓");
            moveUp.setOnAction(x->{
                Util.swapWithPrevious(node.getParent().getChildren(),node);
                VBox parentContainer;
                try {
                    parentContainer =  (VBox) container.getParent().getParent().getParent().getParent().getParent();
                } catch (Exception e) {
                    parentContainer =  (VBox) container.getParent().getParent().getParent().getParent();
                }
                updateElement(node.getParent(), parentContainer );

                applicationController.edit = true;
                applicationController.updateTitle();
            });
            moveDown.setOnAction(x->{
                Util.swapWithNext(node.getParent().getChildren(),node);
                VBox parentContainer;
                try {
                    parentContainer =  (VBox) container.getParent().getParent().getParent().getParent().getParent();
                } catch (Exception e) {
                    parentContainer =  (VBox) container.getParent().getParent().getParent().getParent();
                }
                updateElement(node.getParent(), parentContainer );
                applicationController.edit = true;
                applicationController.updateTitle();
            });
            removeBtn.setOnAction(x->{
                node.getParent().getChildren().remove(node);
                updateElement(node.getParent(), (VBox) container.getParent().getParent().getParent().getParent().getParent());

                applicationController.edit = true;
                applicationController.updateTitle();
            });

            // Inner VBox with Label
            innerVBox.setAlignment(Pos.TOP_CENTER);
            innerVBox.setSpacing(3.0);
            innerVBox.setPadding(new Insets(10.0));
            String name = DisplayHelper.getDisplayNameOfNode(node);
            Label enumerateLabel = new Label(name);
            enumerateLabel.setMaxWidth(Double.MAX_VALUE);
            enumerateLabel.setStyle("-fx-alignment: center;");
            enumerateLabel.setStyle("-fx-font-size: 20px");
            enumerateLabel.setWrapText(true);
            toolBar.getItems().addAll(removeBtn,moveUp,moveDown);


            for (String s : node.getListOfQuantities()) {
                Label Name = new Label(s);
                innerVBox.getChildren().add(Name);
                Control field;
                if (node.getQuantityType(s).equals("IMAGE_REFERENCE")) {
                    field = new ComboBox<String>();
                    ((ComboBox<String>) field).getItems().addAll(applicationController.currProject.getListOfImages());
                    if(!node.getQuantityValue(s).isEmpty()){
                        ((ComboBox<String>) field).setValue(node.getQuantityValue(s));
                    }
                    ((ComboBox)field).setOnAction(x->{
                        node.getQuantity(s).setValue(((ComboBox<String>)field).getValue());
                        applicationController.edit = true;
                        applicationController.updateTitle();
                    });
                }
                else if (node.getQuantityType(s).equals("boolean")) {
                    field = new CheckBox(s);
                    innerVBox.getChildren().remove(Name);
                    ((CheckBox)field).setAllowIndeterminate(false);
                    ((CheckBox)field).setOnAction(x->{
                        node.getQuantity(s).setValue(((CheckBox)field).isSelected()+"");
                        applicationController.edit = true;
                        applicationController.updateTitle();
                    });
                }
                else if (node.getQuantityType(s).equals("textBox")){
                    field = new TextArea(node.getQuantityValue(s));
                    field.setMinHeight(100);
                    field.setOnKeyTyped(event -> {

                        node.getQuantity(s).setValue(((TextArea)field).getText());
                        applicationController.edit = true;
                        applicationController.updateTitle();

                    });

                }
                else {
                     field = new TextField(node.getQuantityValue(s));

                field.setOnKeyTyped(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {

                        node.getQuantity(s).setValue(((TextField)field).getText());
                        applicationController.edit = true;
                        applicationController.updateTitle();
                    }
                });
            }
                innerVBox.getChildren().addAll( field);
                Name.setLabelFor(field);
            }
            container.getChildren().clear();
            container.getChildren().addAll(toolBar, innerVBox);

        }


        if(node instanceof MainbDocument || DisplayHelper.haveChild(node)){
            VBox l = new VBox();
            for(LaTexNode childNode:node.getChildren()){
                VBox inside = GUI_Controller.createElement(childNode,node);
                inside.setPadding(new Insets(0));

                BorderPane newPane= new BorderPane(inside, new ToolBar(  new Label(DisplayHelper.getDisplayNameOfNode(childNode))),null,null,null);

                l.getChildren().add(newPane);
            }
            innerVBox.getChildren().add(l);
            innerVBox.getChildren().add(GUI_Controller.createAddButtonContainer(node,container));

        }



    }

    public static String openFile(Stage s){
        return openFile(s,"Open File","LWC files only","*.LWC");
    }
    public static String openFile(Stage s,String title,String extText,String fileExt){

        FileChooser fil_chooser = new FileChooser();
        fil_chooser.setTitle(title);
        FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter(extText,fileExt);
        fil_chooser.getExtensionFilters().add(ext);
        File f = fil_chooser.showOpenDialog(s);

        if(f!= null){
            return f.getAbsolutePath();
        }
       return null;

    }
    public static String saveFile(Stage s){
        return saveFile(s,"Save File");
    }
    public static String saveFile(Stage s,String y){
        FileChooser fil_chooser = new FileChooser();
        fil_chooser.setTitle(y);
        FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("LWC files only","*.LWC");
        fil_chooser.getExtensionFilters().add(ext);
        File f = fil_chooser.showSaveDialog(s);
        if(f!= null){
            return f.getAbsolutePath();
        }
return null;}


    public static void openProjectFileIntoEdditor(Stage stage){
        String filename = (GUI_Controller.openFile(stage));
        if(filename != null) {
            try {
                applicationController.openFile(filename);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    public static void createNewProjectWizard(Stage stage){
        Stage popup = GUI_Controller.createDialogue(Modality.APPLICATION_MODAL,stage);
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Text title = new Text("Create New Project");
        title.setFont(new Font(19));
        root.setPadding(new Insets(12));
        Line seperator  = new Line(-200,0,200,0);
        HBox templateSelector = new HBox(6);
        templateSelector.setAlignment(Pos.CENTER);
        Button openTemplate = new Button("From file");
        Button openTemplateFromDefault = new Button("From Default");
        TextField displayText = new TextField();
        displayText.setEditable(false);
        displayText.setPromptText("Selected File");
        templateSelector.getChildren().addAll(openTemplate,openTemplateFromDefault,displayText);


        HBox newFileLocation = new HBox(6);
        newFileLocation.setAlignment(Pos.CENTER);
        Button openFile = new Button("From file");
        TextField newFileText = new TextField();
        newFileText.setEditable(false);
        newFileText.setPromptText("Selected File");
        newFileLocation.getChildren().addAll(openFile,newFileText);

        Button confirm = new Button("Create Project");

        root.getChildren().addAll(title,seperator,templateSelector,newFileLocation,confirm);


        Scene scene = new Scene(root,500,500);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();











        AtomicReference<String> templateFileName = new AtomicReference<>("");
        openTemplate.setOnAction(x->{
            String text = GUI_Controller.openFile(popup,"Select Template File","LaTex LWC template files","*.tex");
            displayText.setText(text);
            templateFileName.set(text);
        });

        openTemplateFromDefault.setOnAction(x->{
            GUI_Controller.openTemplateDefault(templateFileName,popup,displayText);
        });

        AtomicReference<String> saveLocation = new AtomicReference<>("");
        openFile.setOnAction(x->{
            String name = GUI_Controller.saveFile(popup);
            newFileText.setText(name);
           saveLocation.set(name);
        });

        confirm.setOnAction(x->{
            applicationController.newFile(saveLocation.get(),templateFileName.get());
            popup.close();
        });


    }


    public class Alerts {
        public static Alert createErrorAlert(String message, String buttonText){
            Alert alart = new Alert(Alert.AlertType.ERROR,message,new ButtonType(buttonText));
            return alart;
        }
        public static Alert createConfomrmationAlert(String message){
            return new Alert(Alert.AlertType.CONFIRMATION,message);
        }
    }

    public static void openTemplateDefault(AtomicReference atomicReference,Stage parent ,TextField edit){
        Stage popup = createDialogue(Modality.APPLICATION_MODAL, parent);


            // Display the list of files in a dialog
            VBox popupContent = new VBox(10);
            popupContent.setPadding(new Insets(20));
            Label title = new Label("Select Template File");
            ComboBox<Defaults> fileComboBox = new ComboBox<>(FXCollections.observableArrayList(Defaults.values()));
            Button confirmButton = new Button("OK");


            confirmButton.setOnAction(event -> {
                Defaults selectedFile = fileComboBox.getValue();
                if (selectedFile != null) {
                    atomicReference.set("DEFAULTPAGE."+selectedFile.name());
                    edit.setText(selectedFile.name());
                    popup.hide();
                }
            });

            popupContent.getChildren().addAll(title, fileComboBox, confirmButton);

            popup.setScene(new Scene(popupContent));
            popup.setTitle("Select Template");
            popup.showAndWait();

    }
    public static String convertImageFromFileSystemToString(Stage fileChooserParentStage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Source Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(fileChooserParentStage);

        if (selectedFile != null) {
            try {
                // A. Encode image file to Text (Base64)
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

return encodedString;



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Image decodeImageFromString(String encodedString){
        // B. Decode Text back to Image for Display
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        Image image = new Image(bis);
        return image;
    }

    public static void mangeImagesWindow(){
        Stage parentStage = applicationController.getCurStage();
        Stage dialouge = createDialogue(Modality.APPLICATION_MODAL,parentStage);


        VBox page = new VBox(10);
        page.getChildren().add(new Text("List of images in file"));
        Accordion imageAccordion = getImageManagerAccordion(page);
        ScrollPane root = new ScrollPane(page);
        root.setFitToWidth(true);

        page.getChildren().add(imageAccordion);
        dialouge.setScene(new Scene(root,500,520));
        dialouge.show();
    }
    public static Accordion getImageManagerAccordion(VBox parent){
        Accordion imageAccordion = new Accordion();
        List<String> imageList = applicationController.currProject.getListOfImages();
        for(String imageName:imageList){
            Image image = decodeImageFromString(applicationController.currProject.getImage(imageName));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(500);
            imageView.setFitHeight(500);
            imageView.setPreserveRatio(true);


            VBox contentBox = new VBox(10);
            ButtonBar bar = new ButtonBar();
            Button removeButton = getRemoveImageButton(parent, imageName);
            bar.getButtons().addAll(removeButton);



            contentBox.getChildren().addAll(imageView,bar);



            imageAccordion.getPanes().add(new TitledPane(imageName,  contentBox ));
        }
        return imageAccordion;
    }

    private static Button getRemoveImageButton(VBox parent, String imageName) {
        Button removeButton = new Button("Remove Image");
        removeButton.setOnAction(Event->{
            applicationController.currProject.removeImageIfExists(imageName);
            applicationController.currProject.getDocHome().getAllNodes().parallelStream().forEach(Node->{
                if(Node.getQuantity("Image Reference") !=null){
                    if(Node.getQuantityValue("Image Reference").equals(imageName)) Node.setQuantityValue("Image Reference","");
                }
            });
            applicationController.updateWholeScreen();
            parent.getChildren().removeIf(screenElement ->screenElement instanceof Accordion);
            parent.getChildren().add(getImageManagerAccordion(parent));

        });
        return removeButton;
    }

    public static String showInputAlert(String title, String content) {
        // 1. Create the dialog with a default value (empty string)
        TextInputDialog dialog = new TextInputDialog("");

        // 2. Set the properties of the dialog
        dialog.setTitle(title);
        dialog.setHeaderText(null); // Set to null to remove the header area
        dialog.setContentText(content);

        // 3. Show the dialog and wait for a response
        // result will be an Optional because the user might cancel
        Optional<String> result = dialog.showAndWait();

        // 4. Return the string if present, otherwise return null
        return result.orElse(null);
    }


}

