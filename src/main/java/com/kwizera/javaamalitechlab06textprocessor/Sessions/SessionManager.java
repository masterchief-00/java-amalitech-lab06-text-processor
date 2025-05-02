package com.kwizera.javaamalitechlab06textprocessor.Sessions;

import java.nio.file.Files;
import java.nio.file.Path;

public class SessionManager {
    private static Path activeDirectory;

    public static Path getActiveDirectory() {
        return activeDirectory;
    }

    public static void setActiveDirectory(Path path) {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Not a directory: " + path);
        }
        activeDirectory = path;
    }
}
