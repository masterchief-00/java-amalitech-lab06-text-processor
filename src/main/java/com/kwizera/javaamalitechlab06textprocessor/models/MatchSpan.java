package com.kwizera.javaamalitechlab06textprocessor.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MatchSpan span = (MatchSpan) o;
        return start == span.start && end == span.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
