package com.kwizera.javaamalitechlab06textprocessor.controllers;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidActiveDirectoryException;
import com.kwizera.javaamalitechlab06textprocessor.Sessions.SessionManager;
import com.kwizera.javaamalitechlab06textprocessor.utils.MainUtilities;
import com.kwizera.javaamalitechlab06textprocessor.utils.UserInterfaceUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LandingPageController {
    private final MainUtilities mainUtilities = new MainUtilities();
    private final UserInterfaceUtilities UIUtilities = new UserInterfaceUtilities();
    private File selectedDirectory;
    private SessionManager session;
    private Path recentFile = Paths.get(System.getProperty("user.home"), "recent_dirs.txt");
    private Path recentDirSelected;

    @FXML
    public Button chooseDirBtn;

    @FXML
    public ListView<String> recentDirsList;

    @FXML
    public Button continueBtn;

    @FXML
    public Text noRecentDirsLabel;

    @FXML
    private void handlePickDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecting working directory");

        Stage stage = (Stage) chooseDirBtn.getScene().getWindow();

        selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            Path selectedPath = selectedDirectory.toPath();
            try {
                session.setActiveDirectory(selectedPath);
            } catch (IllegalArgumentException e) {
                UIUtilities.displayError("Error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onContinueClicked() {
        try {
            if (selectedDirectory == null) {
                throw new InvalidActiveDirectoryException("Invalid directory selected, please pick a different directory!");
            } else {
                saveRecentDirectory(selectedDirectory.toString());
                mainUtilities.switchScene("/com/kwizera/javaamalitechlab06textprocessor/views/main-page.fxml", continueBtn, "TXT Processor | Main page");
            }
        } catch (IOException e) {
            UIUtilities.displayError("Error: Could not load the main page!");
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        initializeData();
        addListeners();
    }

    private void initializeData() {
        session = SessionManager.getInstance();
        syncDirList();
    }

    private void addListeners() {
        recentDirsList.getSelectionModel().selectedItemProperty().addListener((obs, newVal, oldVal) -> {
            if (newVal != null) {
                recentDirSelected = Path.of(newVal);
                selectedDirectory = recentDirSelected.toFile();
                session.setActiveDirectory(selectedDirectory.toPath());
                System.out.println(newVal);
            }
        });
    }

    private List<String> loadRecentDirectories() {
        try {
            if (!Files.exists(recentFile)) return new ArrayList<>();
            return Files.readAllLines(recentFile);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveRecentDirectory(String newDirPath) {
        try {
            List<String> dirs = loadRecentDirectories();

            dirs.remove(newDirPath);
            dirs.add(newDirPath);
            if (dirs.size() > 3) dirs = dirs.subList(0, 3);

            Files.write(recentFile, dirs);
            syncDirList();
        } catch (Exception e) {
            System.out.println("directory not saved");
        }
    }

    private void syncDirList() {
        List<String> recentOpenedDir = loadRecentDirectories();

        if (!recentOpenedDir.isEmpty()) {
            noRecentDirsLabel.setVisible(false);
            recentDirsList.getItems().setAll(recentOpenedDir);

        } else {
            noRecentDirsLabel.setVisible(true);
        }
    }
}
