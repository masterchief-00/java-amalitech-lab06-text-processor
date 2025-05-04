package com.kwizera.javaamalitechlab06textprocessor.controllers;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidActiveDirectoryException;
import com.kwizera.javaamalitechlab06textprocessor.Sessions.SessionManager;
import com.kwizera.javaamalitechlab06textprocessor.utils.MainUtilities;
import com.kwizera.javaamalitechlab06textprocessor.utils.UserInterfaceUtilities;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
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
    public VBox recentDirsBox;


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
            navigateToMainPage();
        } catch (IOException e) {
            UIUtilities.displayError("Error: Could not load the main page!");
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        initializeData();
    }

    private void initializeData() {
        session = SessionManager.getInstance();
        syncDirList();
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
        List<String> recentOpenedDirs = loadRecentDirectories();

        if (!recentOpenedDirs.isEmpty()) {
            noRecentDirsLabel.setVisible(false);

            for (String path : recentOpenedDirs) {

                String normalStyle = """
                            -fx-background-color:  #fff;
                            -fx-border-color:  #d3d3d3;
                            -fx-border-radius: 5;
                            -fx-border-width: 1;
                            -fx-text-fill: #4c4c4c;
                            -fx-alignment: CENTER_LEFT;
                            -fx-padding: 3 8 3 8;
                            -fx-font-family: Segoe UI;
                            -fx-font-size: 11px;
                        """;

                String hoverStyles = """
                            -fx-background-color:  #f8f8ff;
                            -fx-border-color:  #d3d3d3;
                            -fx-border-radius: 5;
                            -fx-border-width: 1;
                            -fx-text-fill: #4c4c4c;
                            -fx-alignment: CENTER_LEFT;
                            -fx-padding: 3 8 3 8;
                            -fx-font-family: Segoe UI;
                            -fx-font-size: 11px;
                        """;

                Button dirButton = new Button(path);
                dirButton.setStyle(normalStyle);
                dirButton.setMaxWidth(Double.MAX_VALUE);
                dirButton.setCursor(Cursor.HAND);
                dirButton.setOnAction(e -> handleDirectorySelection(Path.of(path)));

                dirButton.setOnMouseEntered(e -> dirButton.setStyle(hoverStyles));
                dirButton.setOnMouseExited(e -> dirButton.setStyle(normalStyle));

                recentDirsBox.getChildren().add(dirButton);
            }
        } else {
            noRecentDirsLabel.setVisible(true);
        }
    }

    private void handleDirectorySelection(Path path) {
        try {
            selectedDirectory = path.toFile();
            session.setActiveDirectory(path);
            navigateToMainPage();
        } catch (IOException e) {
            UIUtilities.displayError("Error: Could not load the main page!");
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
        }
    }

    private void navigateToMainPage() throws InvalidActiveDirectoryException, IOException {
        if (selectedDirectory == null) {
            throw new InvalidActiveDirectoryException("Invalid directory selected, please pick a different directory!");
        } else {
            saveRecentDirectory(selectedDirectory.toString());
            mainUtilities.switchScene("/com/kwizera/javaamalitechlab06textprocessor/views/main-page.fxml", continueBtn, "TXT Processor | " + selectedDirectory.toString());
        }
    }
}
