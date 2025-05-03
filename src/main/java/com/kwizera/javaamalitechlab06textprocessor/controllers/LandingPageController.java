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
import java.nio.file.Path;

public class LandingPageController {
    private final MainUtilities mainUtilities = new MainUtilities();
    private final UserInterfaceUtilities UIUtilities = new UserInterfaceUtilities();
    private File selectedDirectory;
    SessionManager session;

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
            if (selectedDirectory == null)
                throw new InvalidActiveDirectoryException("Invalid directory selected, please pick a different directory!");

            mainUtilities.switchScene("/com/kwizera/javaamalitechlab06textprocessor/views/main-page.fxml", continueBtn, "TXT Processor | Main page");
        } catch (IOException e) {
            UIUtilities.displayError("Error: Could not load the main page!");
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        session = SessionManager.getInstance();
    }
}
