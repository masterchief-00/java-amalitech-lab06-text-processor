package com.kwizera.javaamalitechlab06textprocessor.models;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileSearchResult that = (FileSearchResult) o;
        return Objects.equals(filePath, that.filePath) && Objects.equals(matchedLines, that.matchedLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, matchedLines);
    }
}
