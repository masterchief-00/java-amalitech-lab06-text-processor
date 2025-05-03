package com.kwizera.javaamalitechlab06textprocessor.repositories;

import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface TextFileRepository {
    List<TextFile> findAll();

    Optional<TextFile> findByName(String fileName);

    void save(Path path) throws IOException;

    void delete(String file);

    void syncFromDirectory(Path dir) throws IOException;
}
