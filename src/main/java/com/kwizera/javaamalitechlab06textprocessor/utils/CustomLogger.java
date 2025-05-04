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

public class CustomLogger {

    public enum LogLevel {
        INFO, ERROR, WARN
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_FILE = Paths.get(System.getProperty("user.home"), "text_processor_logs.txt");

    public static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = "LOG > [" + timestamp + "] [" + level + "] " + message;
        System.out.println(logEntry);

        try (FileWriter fw = new FileWriter(String.valueOf(LOG_FILE), true)) {
            fw.write(logEntry + "\n");
        } catch (IOException e) {
            System.out.println("LOG > [" + timestamp + "] [ERROR] Failed to write to log file, IOException");
        }
    }
}
