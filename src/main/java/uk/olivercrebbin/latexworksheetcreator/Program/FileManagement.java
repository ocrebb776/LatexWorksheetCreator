package uk.olivercrebbin.latexworksheetcreator.Program;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Collections;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.olivercrebbin.latexworksheetcreator.GUIController.GUI_Controller;

import java.nio.file.StandardCopyOption;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class FileManagement {
        public static List<String> readFileIntoList(String filePath) {
            List<String> lines = Collections.emptyList();
            try {
                // Converts the file path string to a Path object
                Path path = Paths.get(filePath);

                // Reads all lines from the file into a List<String>
                // specifying UTF-8 encoding is a best practice
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Handle file not found or read errors
                e.printStackTrace();
            }
            return lines;
        }
    public static boolean writeToFile(String filePath, List<String> lines) {
            boolean sucsess = false;
        try {
            Path path = Paths.get(filePath);

            // Writes the list to the file using UTF-8
            // By default, this overwrites the file
            Files.write(path, lines, StandardCharsets.UTF_8);
sucsess = true;
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sucsess;
    }
    public static boolean writeToFile(
            String filePath,
            String content
    ){
        boolean sucsess = false;
        try {
            Path path = Paths.get(filePath);

            // Writes the list to the file using UTF-8
            // By default, this overwrites the file
            Files.write(path, Collections.singleton(content), StandardCharsets.UTF_8);
            sucsess = true;
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sucsess;
    }
    public static String runTerminal(String command) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
        }

        proc.waitFor();
        return "test";
    }
    public static boolean promptAndMovePdf(Stage ownerStage, File sourcePdf) {
        // 1. Check if source exists
        if (sourcePdf == null || !sourcePdf.exists()) {
            System.err.println("Error: Source file does not exist.");
            return false;
        }

        // 2. Setup the JavaFX File Chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF As");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Optional: Set initial directory to the source file's folder
        String downloadsPath = System.getProperty("user.home") + "/Downloads";
        File downloadsDir = new File(downloadsPath);

        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            fileChooser.setInitialDirectory(downloadsDir);
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        // 3. Show the Save Dialog
        File destFile = fileChooser.showSaveDialog(ownerStage);

        if (destFile != null) {
            // 4. Ensure destination has .pdf extension
            // (JavaFX often handles this, but this is a safe fallback)
            String destPath = destFile.getAbsolutePath();
            if (!destPath.toLowerCase().endsWith(".pdf")) {
                destFile = new File(destPath + ".pdf");
            }

            // 5. Move and Rename
            try {
                Files.move(sourcePdf.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Success! File moved to: " + destFile.getAbsolutePath());
                return true;
            } catch (IOException e) {
                System.err.println("Error moving file: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("Save command cancelled by user.");
            return false;
        }
    }
    public static void saveImageToDir(File fileToSave,String image){
        if (fileToSave != null) {
            try {
                // SwingFXUtils converts the JavaFX Image object to a standard AWT BufferedImage
                // This is required for ImageIO.write to function.
                BufferedImage bImage = SwingFXUtils.fromFXImage(GUI_Controller.decodeImageFromString(image), null);

                // Determine format based on extension (default to png)
                String fileName = fileToSave.getName().toLowerCase();
                String format = fileName.endsWith("jpg") || fileName.endsWith("jpeg") ? "jpg" : "png";

                // Write the file to disk
                ImageIO.write(bImage, format, fileToSave);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
