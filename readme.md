# LAB: Automated Text Processor

## Overview

An automated text-processing system to streamline data handling, introduce automation, and ensure scalability,
eliminating delays and improving accuracy.
This text processor uses regex patterns to carry out search operations and replace file contents efficiently, not to
mention basic file handling features such as creating, editing, retrieving and deleting files.

## Features

All features are implemented to be accessed on JavaFX user interface.

- Choosing/switching directory
- Loading recently opened directories
- Creating a file
- Save file, Save file as
- Delete file
- Search in file (using regex)
- Search in all files (active directory)
- Replacing all words that match a regex pattern
- Logging to file
- A decent user interface

## Stack

- Java 21
- JavaFX (Scenebuilder,FXML)
- Maven (for dependency management)

## Business logic

- The program follows an MVC structure, `TextFile` defines basic attributes a file needs, basically the metadata.
- The `TextFileRepository` interface defines method signatures such as `findByName`, `save`, `delete` and so on to
  govern how the persistence layer interacts with the service layer.
- The service layer connects the persistence layer and the controller layer using the `TextProcessor` interface. This
  interface declares method signatures such as `readContents(String fileName)`,`search(String regex, String fileName)`,
  `replaceInFile(String regex, String replacement, String fileName)`, etc.
- Since this program needs to run across two pages, it implements a singleton class `SessionManager` which holds
  references to the service layer and persistence layer interfaces across the those pages.

## User interface

- Simple designs
- The landing page prompts the user select a directory to work with in the whole session.
- The main page is accessed after picking a directory, here files in the selected directory are listed.
- A user can select from this select to display contents of file.
- Editing is toggled by clicking on the reader component.
- The main page also has controls to create, save, delete and pick a new directory

## Searching features

Two types of searching have been implemented in this program

- The basic search uses the provided regex pattern to highlight the found matching words.
- In case no results are found in the selected file, the interface prompts the user to choose if they want to do a full
  directory search.
- On full directory search, a file name is displayed with the lines having the matching words

## Replace features

- The UI controls for replacing words in a file are hidden on load.
- They are displayed right after the regex search has found matching words in a file,
- This is where a user can type the replacing word(s).

## Future improvements and enhancements

- With more time, tests can also be added.

## How to run

- Clone the project
- Open in intelliJ IDEA or another JavaFX-Compatible IDE
- Maven adds dependencies automatically
- Run the `Main` class
- 👍