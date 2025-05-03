package com.kwizera.javaamalitechlab06textprocessor.controllers;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidActiveDirectoryException;
import com.kwizera.javaamalitechlab06textprocessor.Sessions.SessionManager;
import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;
import com.kwizera.javaamalitechlab06textprocessor.services.DataManager;
import com.kwizera.javaamalitechlab06textprocessor.services.TextProcessor;
import com.kwizera.javaamalitechlab06textprocessor.utils.UserInterfaceUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MainPageController {
    public Button deleteFileBtn;
    SessionManager session;
    private final UserInterfaceUtilities UIUtilities = new UserInterfaceUtilities();
    private TextProcessor textProcessor;
    private Path activeDirectory;
    List<TextFile> allFiles = new ArrayList<>();

    private String fileNameSelected;

    @FXML
    public Button newFileBtn;

    @FXML
    public Button saveBtn;

    @FXML
    public Button saveAsBtn;

    @FXML
    public Button reloadBtn;

    @FXML
    public Button backHomeBtn;

    @FXML
    public ListView<String> dirFilesList;

    @FXML
    public TextField regexInput;

    @FXML
    public Button regexSearchBtn;

    @FXML
    public TextFlow textReader;

    @FXML
    public void onDeleteFileClicked() {
        boolean choice = UIUtilities.displayWarning("You are about to delete " + fileNameSelected + ", this action is irreversible. Sure?", "Delete " + fileNameSelected);
        if (choice) {
            try {
                if (textProcessor.deleteFile(fileNameSelected)) {
                    UIUtilities.displayConfirmation("File deleted");
                    syncFileList();
                }
            } catch (IOException e) {
                UIUtilities.displayError("ERROR: File not deleted");
            }

        }
    }

    @FXML
    public void onNewFileClicked() {
        Optional<String> fileName = UIUtilities.fileNamingDialogBox();

        if (fileName.isPresent()) {
            try {
                Path filePath = activeDirectory.resolve(fileName.get() + ".txt");
                textProcessor.createFile(filePath);
                UIUtilities.displayConfirmation("File created successfully");
                syncFileList();
            } catch (IOException e) {
                UIUtilities.displayError("ERROR: File could not be created, check if the file with similar isn't already present");
            }
        } else {
            UIUtilities.displayError("ERROR: File not created, invalid file name.");
        }
    }

    @FXML
    private void initialize() {
        try {
            initializeData();
            addListeners();
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: Files in the selected directory could not be loaded, please pick a different directory.");
        }
    }

    private void addListeners() {
        dirFilesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fileNameSelected = newVal;
                loadFileContents(newVal);
            }
        });
    }

    private void initializeData() throws InvalidActiveDirectoryException, IOException {
        session = SessionManager.getInstance();
        textProcessor = session.getServices();
        activeDirectory = session.getActiveDirectory();
        syncFileList();
    }

    private void syncFileList() throws IOException {
        textProcessor.syncFiles(activeDirectory);
        allFiles = textProcessor.getFilesInDirectory();

        if (!allFiles.isEmpty()) {
            dirFilesList.getItems().clear();
            allFiles.forEach(textFile -> {
                dirFilesList.getItems().add(textFile.getName());
            });
        } else {
            dirFilesList.getItems().add("<empty directory>");
        }
    }

    private void loadFileContents(String fileName) {
        try {
            textReader.getChildren().clear();
            Object contents = textProcessor.readContents(fileName);
            if (contents instanceof String fullText) {
                Text textNode = new Text(fullText + "\n");
                textReader.getChildren().add(textNode);
            } else if (contents instanceof Stream<?> stream) {
                try (Stream<String> lines = (Stream<String>) stream) {
                    lines.forEach(line -> {
                        Text textnode = new Text(line + "\n");
                        textReader.getChildren().add(textnode);
                    });
                }
            }
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: file contents couldn't be loaded!");
        }
    }
}
