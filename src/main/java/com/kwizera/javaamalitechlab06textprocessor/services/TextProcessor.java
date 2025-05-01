package com.kwizera.javaamalitechlab06textprocessor.services;

import com.kwizera.javaamalitechlab06textprocessor.models.LineMatchResult;

import java.nio.file.Path;
import java.util.List;

public interface TextProcessor {
    List<LineMatchResult> search(String regex, List<String> lines);

    List<LineMatchResult> massiveSearch(String regex, Path directory);
}
