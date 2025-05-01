package com.kwizera.javaamalitechlab06textprocessor.models;

public class MatchSpan {
    private final int start;
    private final int end;

    public MatchSpan(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }
}
