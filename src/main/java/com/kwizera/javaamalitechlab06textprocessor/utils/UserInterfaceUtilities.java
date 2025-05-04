package com.kwizera.javaamalitechlab06textprocessor.utils;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidFilenameException;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;


// utility class containing dialog boxes for errors, confirmations and warnings
public class UserInterfaceUtilities {
    InputValidationUtilities inputValidationUtilities = new InputValidationUtilities();

    public Optional<String> fileNamingDialogBox() {
        Dialog<String> fileNamingDialog = new Dialog<>();
        fileNamingDialog.setTitle("File naming");
        fileNamingDialog.setHeaderText("Enter a file name");

        Label fileNameErrorLabel = new Label("Invalid input, a valid file name can only have alphanumeric characters.");
        fileNameErrorLabel.setStyle("-fx-text-fill: red;");
        fileNameErrorLabel.setWrapText(true);
        fileNameErrorLabel.setMinSize(100, 50);
        fileNameErrorLabel.setVisible(false);

        // buttons
        ButtonType fileNamingButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        fileNamingDialog.getDialogPane().getButtonTypes().addAll(fileNamingButtonType, ButtonType.CANCEL);

        GridPane fileNamingGrid = new GridPane();
        fileNamingGrid.setHgap(10);
        fileNamingGrid.setVgap(10);
        fileNamingGrid.setPadding(new Insets(20, 150, 10, 10));

        // min rating input
        TextField fileNameInput = new TextField();
        fileNameInput.setPromptText("e.g: employees.txt");

        fileNamingGrid.add(new Label("File name:"), 0, 0);
        fileNamingGrid.add(fileNameInput, 1, 0);
        fileNamingGrid.add(fileNameErrorLabel, 0, 1, 2, 1);

        fileNamingDialog.getDialogPane().setContent(fileNamingGrid);

        // input validation
        Node fileNamingSubmitButton = fileNamingDialog.getDialogPane().lookupButton(fileNamingButtonType);
        fileNamingSubmitButton.setDisable(true);

        ChangeListener<String> validationListener = (observable, oldValue, newValue) -> {
            String newFileNameText = fileNameInput.getText();

            try {
                if (inputValidationUtilities.invalidFileName(newFileNameText))
                    throw new InvalidFilenameException("Invalid input, a valid file name can only have alphanumeric characters.");

                fileNameErrorLabel.setText("");
                fileNamingSubmitButton.setDisable(false);
            } catch (InvalidFilenameException e) {
                if (!newFileNameText.isEmpty()) {
                    fileNameErrorLabel.setText(e.getMessage());
                    fileNameErrorLabel.setVisible(true);
                } else {
                    fileNameErrorLabel.setText("");
                }

                fileNamingSubmitButton.setDisable(true);
            }
        };

        fileNameInput.textProperty().addListener(validationListener);

        fileNamingDialog.setResultConverter(dialogButton -> {
            if (dialogButton == fileNamingButtonType) {
                return fileNameInput.getText();
            }
            return null;
        });

        return fileNamingDialog.showAndWait();
    }

    public boolean displayWarning(String message, String promptTitle) {
        Alert alert = new Alert(Alert.AlertType.WARNING,
                message,
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(promptTitle);

        final boolean[] choice = {false};

        alert.showAndWait().ifPresent(response -> {
            choice[0] = response == ButtonType.YES;
        });

        return choice[0];
    }

    // a utility to render error messages
    public void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // utility method to render confirmation messages
    public void displayConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Completed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
