package com.ra2.users.ra2_users.loggins;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomLoggin {

    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOG_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // escriure info normal
    public void logInfo(String className, String methodName, String description) {
        String timestamp = LocalDateTime.now().format(LOG_DATETIME_FORMAT);
        String logEntry = String.format("[%s] INFO - %s - %s - %s", 
                                       timestamp, className, methodName, description);
        
        System.out.println(logEntry);
        writeToFile(logEntry);
    }

    // escriure errors
    public void logError(String className, String methodName, String description, Exception exception) {
        String timestamp = LocalDateTime.now().format(LOG_DATETIME_FORMAT);
        String errorMsg = exception != null ? exception.getMessage() : "Error desconegut";
        String logEntry = String.format("[%s] ERROR - %s - %s - %s. Exception: %s", 
                                       timestamp, className, methodName, description, errorMsg);
        
        System.out.println(logEntry);
        writeToFile(logEntry);
    }

    // escriure fisicament al fitxer
    private void writeToFile(String logEntry) {
        try {
            // crear carpeta logs si no existeix
            Path logDirectory = Paths.get(LOG_DIR);
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }

            // nom fitxer del dia
            String fileName = "application-" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".log";
            Path logFilePath = logDirectory.resolve(fileName);

            // escriure amb append
            try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.APPEND)) {
                writer.write(logEntry);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error escrivint al fitxer de log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}