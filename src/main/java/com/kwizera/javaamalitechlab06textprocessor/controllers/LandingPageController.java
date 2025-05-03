package com.kwizera.javaamalitechlab06textprocessor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class LandingPageController {
    @FXML
    public Button chooseDirBtn;

    @FXML
    public ListView<String> recentDirsList;

    @FXML
    public Button continueBtn;

    @FXML
    public Text noRecentDirsLabel;

    @FXML
    private void initialize() {

    }
}
