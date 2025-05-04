package com.kwizera.javaamalitechlab06textprocessor.controllers;

import com.kwizera.javaamalitechlab06textprocessor.Exceptions.InvalidActiveDirectoryException;
import com.kwizera.javaamalitechlab06textprocessor.Sessions.SessionManager;
import com.kwizera.javaamalitechlab06textprocessor.models.FileSearchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.LineMatchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.MatchSpan;
import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;
import com.kwizera.javaamalitechlab06textprocessor.services.DataManager;
import com.kwizera.javaamalitechlab06textprocessor.services.TextProcessor;
import com.kwizera.javaamalitechlab06textprocessor.utils.CustomLogger;
import com.kwizera.javaamalitechlab06textprocessor.utils.InputValidationUtilities;
import com.kwizera.javaamalitechlab06textprocessor.utils.MainUtilities;
import com.kwizera.javaamalitechlab06textprocessor.utils.UserInterfaceUtilities;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public class MainPageController {

    SessionManager session;
    private final UserInterfaceUtilities UIUtilities = new UserInterfaceUtilities();
    private final MainUtilities mainUtilities = new MainUtilities();
    private final InputValidationUtilities inputValidationUtilities = new InputValidationUtilities();
    private TextProcessor textProcessor;
    private Path activeDirectory;
    List<TextFile> allFiles = new ArrayList<>();

    private String fileNameSelected;
    private String currentRegex;

    @FXML
    public HBox searchDeleteBox;
    @FXML

    public HBox replaceBox;
    @FXML

    public TextField replaceInput;
    @FXML

    public Button replaceBtn;

    @FXML
    public VBox fileSearchResultsBox;

    @FXML
    public TextArea textWriter;

    @FXML
    public Button deleteFileBtn;

    @FXML
    public StackPane editorContainer;

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

    // implements the functionality to replace file contents using a regex
    @FXML
    public void onReplaceClicked() {
        if (!replaceInput.getText().isEmpty()) {
            try {
                int replacedCount = textProcessor.replaceInFile(currentRegex, replaceInput.getText(), fileNameSelected);
                UIUtilities.displayConfirmation(replacedCount + " changes applied");
                loadFileContents(fileNameSelected, false);
                replaceBox.setVisible(false);
                searchDeleteBox.setVisible(true);
            } catch (IOException e) {
                UIUtilities.displayError("ERROR: Unable to perform replace operation");
                CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to perform replace operation,  IOException");
            }
        }
    }

    // implements functionality to allow searching by regex pattern
    @FXML
    public void onRegexSearchClicked() {
        try {
            currentRegex = regexInput.getText();
            List<LineMatchResult> matchResults = textProcessor.search(currentRegex, fileNameSelected);
            int matchCount = 0;
            if (!matchResults.isEmpty()) {
                matchCount = matchResults.stream().mapToInt(result -> result.getMatches().size()).sum();
                applyHighlighting(matchResults);

                UIUtilities.displayConfirmation("Search complete, " + matchCount + " results found");
                if (matchCount > 0) {
                    searchDeleteBox.setVisible(false);
                    replaceBox.setVisible(true);
                }

            } else {
                boolean choice = UIUtilities.displayWarning("No results found, would you like a directory wide search?", "Directory search");
                if (choice) {
                    directorySearch();
                }
            }

        } catch (IOException e) {
            UIUtilities.displayError("Unable to search for the entered pattern");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to search for the entered pattern, IOException");
        } catch (PatternSyntaxException e) {
            UIUtilities.displayError("ERROR: Invalid regex syntax");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to search for the entered pattern, Invalid regex");
        }
    }

    // implements functionality to save new changes to the selected file
    @FXML
    public void onSaveClicked() {
        if (textWriter.isVisible()) {
            saveChanges();
        } else {
            UIUtilities.displayError("No changes to save");
        }
    }

    // implements functionality to save new changes to a new file
    @FXML
    public void onSaveAsClicked() {
        if (!textWriter.isVisible()) {
            UIUtilities.displayError("No changes to save");
            return;
        }

        Optional<String> fileName = UIUtilities.fileNamingDialogBox();

        if (fileName.isPresent()) {
            try {
                Path filePath = activeDirectory.resolve(fileName.get() + ".txt");
                textProcessor.createFile(filePath);
                String newFileName = filePath.getFileName().toString();

                List<String> lines = List.of(textWriter.getText().split("\\R"));
                textProcessor.saveToFile(newFileName, lines);

                UIUtilities.displayConfirmation("Changes saved to " + newFileName);
                syncFileList();
                defaultSelect(newFileName);
            } catch (IOException e) {
                UIUtilities.displayError("ERROR: File could not be created, check if the file with similar isn't already present");
                CustomLogger.log(CustomLogger.LogLevel.ERROR, "File could not be created, File already exists or another type of IOException");
            }
        } else {
            UIUtilities.displayError("ERROR: File not created, invalid file name.");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "File not created, invalid file name.");
        }
    }

    // navigates back to landing page to select a new directory
    @FXML
    public void onChangeDirectory() {
        try {
            mainUtilities.switchScene("/com/kwizera/javaamalitechlab06textprocessor/views/landing-page.fxml", backHomeBtn, "TXT Processor");
        } catch (Exception e) {
            UIUtilities.displayError("Error: Could not load the main page!");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Could not load the main page, IOException");
        }
    }

    // refreshes the UI to sync with the memory
    @FXML
    public void onReloadClicked() {
        try {
            syncFileList();
            defaultSelect();
            searchDeleteBox.setVisible(true);
            replaceBox.setVisible(false);
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: Unable to reload files");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to reload files, IOException");
        }

    }

    // implements functionality to delete files
    @FXML
    public void onDeleteFileClicked() {
        boolean choice = UIUtilities.displayWarning("This action is irreversible. Sure?", "Delete " + fileNameSelected);
        if (choice) {
            try {
                if (textProcessor.deleteFile(fileNameSelected)) {
                    UIUtilities.displayConfirmation("File deleted");
                    syncFileList();
                    defaultSelect();
                }
            } catch (IOException e) {
                UIUtilities.displayError("ERROR: File not deleted");
                CustomLogger.log(CustomLogger.LogLevel.ERROR, "File not deleted, IOException");
            }

        }
    }

    // implements functionality to create a new file
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
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "File not created, invalid file name.");
        }
    }

    @FXML
    private void initialize() {
        try {
            initializeData();
            addListeners();
        } catch (InvalidActiveDirectoryException e) {
            UIUtilities.displayError("ERROR: " + e.getMessage());
            CustomLogger.log(CustomLogger.LogLevel.ERROR, e.getMessage());
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: Files in the selected directory could not be loaded, please pick a different directory.");
        }
    }

    // attaches listeners to UI controls
    private void addListeners() {
        dirFilesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switchToReadMode();
                fileNameSelected = newVal;
                loadFileContents(newVal, false);
            }
        });

        textReader.setOnMouseClicked(e -> switchToEditMode());

        defaultSelect();

        CustomLogger.log(CustomLogger.LogLevel.INFO, "Attached listeners to files list and text flow");
    }

    // Initializes the session variable, services reference and refreshes the files list
    private void initializeData() throws InvalidActiveDirectoryException, IOException {
        session = SessionManager.getInstance();
        textProcessor = session.getServices();
        activeDirectory = session.getActiveDirectory();
        syncFileList();
        CustomLogger.log(CustomLogger.LogLevel.INFO, "Initialized data on the main page");
    }

    // retrieves files from the active directory. useful in refreshing UI
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

    // loads the contents of the selected file to the text flow component
    private void loadFileContents(String fileName, Boolean isLoadingIntoTextArea) {
        try {
            if (isLoadingIntoTextArea) {
                textWriter.clear();
            } else {
                textReader.getChildren().clear();
            }
            Object contents = textProcessor.readContents(fileName);
            if (contents instanceof String fullText) {

                List<String> lines = List.of(fullText.split("\\R"));

                lines.forEach(line -> {
                    Text textNode = new Text(line + "\n");
                    if (isLoadingIntoTextArea) {
                        textWriter.appendText(line + "\n");

                    } else {
                        textReader.getChildren().add(textNode);
                    }
                });


            } else if (contents instanceof Stream<?> stream) {
                try (Stream<String> lines = (Stream<String>) stream) {
                    lines.forEach(line -> {
                        Text textnode = new Text(line + "\n");

                        if (isLoadingIntoTextArea) {
                            textWriter.appendText(line + "\n");
                        } else {
                            textReader.getChildren().add(textnode);
                        }
                    });
                }
            }
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: file contents couldn't be loaded!");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "file contents couldn't be loaded, IOException");
        }
    }

    // disables the text flow, enables the text area to allow the user to make changes
    private void switchToEditMode() {
        textReader.setVisible(false);
        textWriter.setVisible(true);
        loadFileContents(fileNameSelected, true);
        textWriter.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.S) {
                saveChanges();
            }
        });
    }

    // does the opposite of switchToEditMode()
    private void switchToReadMode() {
        textReader.setVisible(true);
        textWriter.setVisible(false);
        fileSearchResultsBox.setVisible(false);
        loadFileContents(fileNameSelected, true);
    }

    // saves edited changes to the active file
    private void saveChanges() {
        try {
            // creates lines by splitting by special line ending characters like \n
            List<String> lines = List.of(textWriter.getText().split("\\R"));
            textProcessor.saveToFile(fileNameSelected, lines);
            UIUtilities.displayConfirmation("File edited, new changes saved successfully");
            switchToReadMode();
            defaultSelect(fileNameSelected);
        } catch (IOException e) {
            UIUtilities.displayError("ERROR: Unable to save file");
            switchToReadMode();
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to save file, IOException");
        }
    }

    // instructs the list view component to select a specific file
    private void defaultSelect(String fileName) {
        loadFileContents(fileName, false);
        dirFilesList.getSelectionModel().select(fileName);
    }

    // instructs the list view component to select the first item
    private void defaultSelect() {
        dirFilesList.getSelectionModel().selectFirst();
    }

    // applies highlights to the matched words
    private void applyHighlighting(List<LineMatchResult> searchResults) {
        textReader.getChildren().clear();
        for (LineMatchResult result : searchResults) {
            int lastIndex = 0;
            String line = result.getLineText();

            for (MatchSpan span : result.getMatches()) {
                if (lastIndex < span.getStart()) {
                    String plainText = line.substring(lastIndex, span.getStart());
                    textReader.getChildren().add(new Text(plainText));
                }

                String matchedText = line.substring(span.getStart(), span.getEnd());
                Label highlighted = new Label(matchedText);
                highlighted.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-background-color: yellow;");
                textReader.getChildren().add(highlighted);

                lastIndex = span.getEnd();
            }

            if (lastIndex < line.length()) {
                textReader.getChildren().add(new Text(line.substring(lastIndex)));
            }

            textReader.getChildren().add(new Text("\n"));
        }
    }

    // performs a directory wide search
    private void directorySearch() {
        try {
            List<FileSearchResult> fileSearchResults = textProcessor.massiveSearch(regexInput.getText());
            int matchCount = 0;

            if (!fileSearchResults.isEmpty()) {
                textReader.setVisible(false);
                textWriter.setVisible(false);
                fileSearchResultsBox.setVisible(true);
                fileSearchResultsBox.prefWidthProperty().bind(editorContainer.widthProperty());
                fileSearchResultsBox.setMaxWidth(Double.MAX_VALUE);
                fileSearchResultsBox.setSpacing(10);
                fileSearchResultsBox.getChildren().clear();

                for (FileSearchResult result : fileSearchResults) {
                    VBox resultsItem = getSingleFileSearchResultBox(result);
                    Label fileNameLabel = new Label("📄 " + result.getFilePath().getFileName().toString());
                    fileNameLabel.setWrapText(true);
                    fileNameLabel.setFont(Font.font("Segoe UI Emoji", 14));
                    fileNameLabel.setStyle("-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill: #45b3e0;");
                    resultsItem.getChildren().add(fileNameLabel);

                    for (LineMatchResult line : result.getMatchedLines()) {
                        String lineInfo = "Line " + line.getLineNumber() + " | " + line.getLineText();
                        Label lineText = new Label(lineInfo);
                        lineText.setStyle("-fx-text-fill:#4c4c4c");
                        lineText.setFont(Font.font("Segoe UI Emoji", 12));
                        lineText.setWrapText(true);
                        resultsItem.getChildren().add(lineText);
                        matchCount++;
                    }
                    fileSearchResultsBox.getChildren().add(resultsItem);
                    resultsItem.prefWidthProperty().bind(fileSearchResultsBox.widthProperty());

                }

                UIUtilities.displayConfirmation("Search complete, " + matchCount + " results found from " + fileSearchResults.size() + " files.");

            } else {
                UIUtilities.displayConfirmation("Search complete, no results found.");

            }
        } catch (Exception e) {
            UIUtilities.displayError("ERROR: Unable to perform the search");
            CustomLogger.log(CustomLogger.LogLevel.ERROR, "Unable to perform regex search in directory wide search");
        }
    }

    private VBox getSingleFileSearchResultBox(FileSearchResult result) {
        String normalStyle = """
                -fx-background-color: #f8f8ff;
                -fx-border-color: #45b3e0;
                -fx-border-width: 1;
                -fx-border-radius:4;
                """;
        String hoverStyle = """
                -fx-background-color: #f5ffff;
                -fx-border-color: #007fff;
                -fx-border-width: 1.2;
                -fx-border-radius:6;
                """;

        VBox resultsItem = new VBox();
        resultsItem.setMaxWidth(Double.MAX_VALUE);
        resultsItem.setSpacing(3);
        resultsItem.setPrefWidth(100);
        resultsItem.setPadding(new Insets(10, 10, 10, 10));
        resultsItem.setStyle(normalStyle);
        resultsItem.setCursor(Cursor.HAND);

        resultsItem.setOnMouseEntered(e -> resultsItem.setStyle(hoverStyle));
        resultsItem.setOnMouseExited(e -> resultsItem.setStyle(normalStyle));
        resultsItem.setOnMouseClicked(e -> {
            defaultSelect(result.getFilePath().getFileName().toString());
            loadFileContents(result.getFilePath().getFileName().toString(), false);
            fileSearchResultsBox.setVisible(false);
            textWriter.setVisible(false);
            textReader.setVisible(true);
        });
        return resultsItem;
    }
}
