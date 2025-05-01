package com.kwizera.javaamalitechlab06textprocessor.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TextFile {
    private String name;
    private long size;
    private long lastModified;
    private boolean isDirectory;
    private final Path path;

    private static final long DEFAULT_SIZE_THRESHOLD = 3 * 1024 * 1024; // 3MB

    public TextFile(String name, long size, long lastModified, boolean isDirectoryFile, Path path) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        this.isDirectory = isDirectoryFile;
        this.path = path;
    }

    // reads content as a stream. line by line
    public Stream<String> getContentFileContentStream() throws IOException {
        return Files.lines(path);
    }

    // reads content as a string at once. not for large files
    public String readAllContent() throws IOException {
        return Files.readString(path);
    }

    public Object readSafe(long fileSizeLimitBytes) throws IOException {
        if (this.size <= fileSizeLimitBytes) return readAllContent();
        else return getContentFileContentStream();
    }

    // overloaded version which uses default threshold
    public Object readSafe() throws IOException {
        return readSafe(DEFAULT_SIZE_THRESHOLD);
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Path getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    @Override
    public String toString() {
        return (isDirectory ? "[DIR] " : "[FILE] ") +
                name + " | Size: " + size + " bytes | Modified: " + lastModified;
    }

}
