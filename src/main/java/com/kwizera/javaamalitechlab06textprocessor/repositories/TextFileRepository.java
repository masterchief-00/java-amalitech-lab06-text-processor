package com.kwizera.javaamalitechlab06textprocessor.repositories;

import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;

import java.util.List;
import java.util.Optional;

public interface TextFileRepository {
    List<TextFile> findAll();

    Optional<TextFile> findByName(String fileName);

    void save(TextFile file);

    void delete(String file);
}
