package com.kwizera.javaamalitechlab06textprocessor.models;

import java.util.List;
import java.util.Objects;

public class LineMatchResult {
    private final int lineNumber;
    private final String lineText;
    private final List<MatchSpan> matches;

    public LineMatchResult(int lineNumber, String lineText, List<MatchSpan> matches) {
        this.lineNumber = lineNumber;
        this.lineText = lineText;
        this.matches = matches;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineText() {
        return lineText;
    }

    public List<MatchSpan> getMatches() {
        return matches;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LineMatchResult that = (LineMatchResult) o;
        return lineNumber == that.lineNumber && Objects.equals(lineText, that.lineText) && Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, lineText, matches);
    }
}
