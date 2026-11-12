package uk.olivercrebbin.latexworksheetcreator.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LatexCompiler {

    /**
     * Compiles a .tex file using pdflatex on macOS.
     *
     * @param absoluteFilePath The absolute path to the .tex file.
     */
    public static String compileTexFile(String absoluteFilePath) {
        File texFile = new File(absoluteFilePath);

        if (!texFile.exists()) {
            System.err.println("Error: File not found at " + absoluteFilePath);
            return null;
        }

        File workingDirectory = texFile.getParentFile();
        String fileName = texFile.getName();

        // calculate the expected PDF file name (replace .tex with .pdf)
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String pdfFileName = baseName + ".pdf";
        File expectedPdfFile = new File(workingDirectory, pdfFileName);

        // Build command: Use absolute path for safety on Mac
        List<String> command = new ArrayList<>();
        command.add("/Library/TeX/texbin/pdflatex"); // Standard MacTeX location
        command.add("-interaction=nonstopmode");
        command.add(fileName);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            // Read output stream to prevent buffer blocking
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Print LaTeX logs to console (optional)
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Double check that the PDF was actually created
                if (expectedPdfFile.exists()) {
                    return expectedPdfFile.getAbsolutePath();
                } else {
                    System.err.println("Error: Compiler finished but PDF was not found.");
                    return null;
                }
            } else {
                System.err.println("Compilation failed with exit code: " + exitCode);
                return null;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}