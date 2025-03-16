package pso;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileWriter {
    private String filePath;

    public FileWriter() {
        this.filePath = "LogPSO.txt";
    }

    public void writeLines(String[] lines) {
        try (BufferedWriter writer = new BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath, false), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Log do Algoritmo realizado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
