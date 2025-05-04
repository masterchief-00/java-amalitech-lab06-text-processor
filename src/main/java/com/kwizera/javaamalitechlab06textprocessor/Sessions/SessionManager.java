package com.kwizera.javaamalitechlab06textprocessor.Sessions;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidActiveDirectoryException;
import com.kwizera.javaamalitechlab06textprocessor.repositories.InMemoryTextFileRepository;
import com.kwizera.javaamalitechlab06textprocessor.repositories.TextFileRepository;
import com.kwizera.javaamalitechlab06textprocessor.services.DataManager;
import com.kwizera.javaamalitechlab06textprocessor.services.TextProcessor;

import java.nio.file.Files;
import java.nio.file.Path;

// singleton class managing the session and the whole application context, holds references to the services and repository interfaces
public class SessionManager {
    private static SessionManager instance = new SessionManager();

    private static Path activeDirectory;
    private final TextFileRepository textFileRepository;
    private final TextProcessor textProcessor;

    private SessionManager() {
        this.textFileRepository = new InMemoryTextFileRepository();
        this.textProcessor = new DataManager(textFileRepository);
    }


    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public TextProcessor getServices() {
        return textProcessor;
    }

    public Path getActiveDirectory() throws InvalidActiveDirectoryException {
        if (activeDirectory == null)
            throw new InvalidActiveDirectoryException("Invalid directory selected, please pick a different directory!");
        return activeDirectory;
    }

    public void setActiveDirectory(Path path) {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Not a directory: " + path);
        }
        activeDirectory = path;
    }
}
