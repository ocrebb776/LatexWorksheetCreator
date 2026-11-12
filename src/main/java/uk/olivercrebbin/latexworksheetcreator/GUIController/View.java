package uk.olivercrebbin.latexworksheetcreator.GUIController;

public enum View {
    LAUNCH("LaunchScreen.fxml"),
    HOME("HomeScreen.fxml");

    private String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
