package com.kwizera.javaamalitechlab06textprocessor.models;

import java.util.List;

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
}
