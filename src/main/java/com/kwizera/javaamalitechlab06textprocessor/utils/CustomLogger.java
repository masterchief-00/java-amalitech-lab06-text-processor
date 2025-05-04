package com.kwizera.javaamalitechlab06textprocessor.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// custom logger to log to console and log file
public class CustomLogger {

    public enum LogLevel {
        INFO, ERROR, WARN
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // log file to be placed at c:\[username]\text_processor_logs.txt
    private static final Path LOG_FILE = Paths.get(System.getProperty("user.home"), "text_processor_logs.txt");

    public static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = "LOG > [" + timestamp + "] [" + level + "] " + message;
        System.out.println(logEntry);

        // try-with-resources initializing a writer in append mode
        try (FileWriter fw = new FileWriter(String.valueOf(LOG_FILE), true)) {
            fw.write(logEntry + "\n");
        } catch (IOException e) {
            System.out.println("LOG > [" + timestamp + "] [ERROR] Failed to write to log file, IOException");
        }
    }
}
