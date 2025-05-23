package com.kwizera.javaamalitechlab06textprocessor.services;

import com.kwizera.javaamalitechlab06textprocessor.models.FileSearchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.LineMatchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface TextProcessor {

    Object readContents(String fileName) throws IOException;

    List<LineMatchResult> search(String regex, String fileName) throws IOException;

    List<FileSearchResult> massiveSearch(String regex) throws IOException;

    int replaceInFile(String regex, String replacement, String fileName) throws IOException;

    void saveToFile(String fileName, List<String> lines) throws IOException;

    void createFile(Path path) throws IOException;

    boolean deleteFile(String fileName) throws IOException;

    void syncFiles(Path dir) throws IOException;

    List<TextFile> getFilesInDirectory();
}
