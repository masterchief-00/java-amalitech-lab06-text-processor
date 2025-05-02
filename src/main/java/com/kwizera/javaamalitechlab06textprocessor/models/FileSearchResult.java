package com.kwizera.javaamalitechlab06textprocessor.models;

import java.nio.file.Path;
import java.util.List;

public class FileSearchResult {
    private final Path filePath;
    private final List<LineMatchResult> matchedLines;

    public FileSearchResult(Path filePath, List<LineMatchResult> matchedLines) {
        this.filePath = filePath;
        this.matchedLines = matchedLines;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<LineMatchResult> getMatchedLines() {
        return matchedLines;
    }

    public boolean hasMatches() {
        return !matchedLines.isEmpty();
    }
}
