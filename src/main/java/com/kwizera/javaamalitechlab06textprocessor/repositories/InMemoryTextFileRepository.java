package com.kwizera.javaamalitechlab06textprocessor.repositories;

import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class InMemoryTextFileRepository implements TextFileRepository {

    private final Map<String, TextFile> store = new HashMap<>();

    @Override
    public List<TextFile> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<TextFile> findByName(String fileName) {
        return Optional.ofNullable(store.get(fileName));
    }

    // checks if a file is valid before adding it to a collection
    @Override
    public void save(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            TextFile textFile = new TextFile(
                    path.getFileName().toString(),
                    Files.size(path),
                    attributes.lastModifiedTime().toMillis(),
                    false, path);
            store.put(textFile.getName(), textFile);
        }
    }

    @Override
    public void delete(String fileName) {
        store.remove(fileName);
    }

    // refreshes the collection in sync with a directory
    @Override
    public void syncFromDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            store.clear();

            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    BasicFileAttributes attributes = Files.readAttributes(entry, BasicFileAttributes.class);
                    TextFile textFile = new TextFile(
                            entry.getFileName().toString(),
                            Files.size(entry),
                            attributes.lastModifiedTime().toMillis(),
                            false, entry);
                    store.put(textFile.getName(), textFile);
                }
            }
        }
    }
}
