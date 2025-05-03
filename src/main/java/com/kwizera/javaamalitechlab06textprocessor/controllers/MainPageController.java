package com.kwizera.javaamalitechlab06textprocessor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

public class MainPageController {

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
    private void initialize() {

    }
}
