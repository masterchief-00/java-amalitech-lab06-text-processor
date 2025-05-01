package com.kwizera.javaamalitechlab06textprocessor.services;

import com.kwizera.javaamalitechlab06textprocessor.models.LineMatchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.MatchSpan;
import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataManager implements TextProcessor {

    @Override
    public List<LineMatchResult> search(String regex, List<String> lines) {
        Pattern pattern = Pattern.compile(regex); // invalid regex exception. custom one?
        List<LineMatchResult> results = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = pattern.matcher(line);
            List<MatchSpan> spans = new ArrayList<>();

            while (matcher.find()) {
                spans.add(new MatchSpan(matcher.start(), matcher.end()));
            }

            if (!spans.isEmpty()) {
                results.add(new LineMatchResult(i, line, spans));
            }
        }

        return results;
    }

    @Override
    public List<LineMatchResult> massiveSearch(String regex, Path directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.txt")) {

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
