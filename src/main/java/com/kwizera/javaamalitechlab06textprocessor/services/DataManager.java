package com.kwizera.javaamalitechlab06textprocessor.services;

import com.kwizera.javaamalitechlab06textprocessor.models.FileSearchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.LineMatchResult;
import com.kwizera.javaamalitechlab06textprocessor.models.MatchSpan;
import com.kwizera.javaamalitechlab06textprocessor.models.TextFile;
import com.kwizera.javaamalitechlab06textprocessor.repositories.TextFileRepository;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public class DataManager implements TextProcessor {

    public final TextFileRepository textFileRepository;

    public DataManager(TextFileRepository textFileRepository) {
        this.textFileRepository = textFileRepository;
    }

    @Override
    public Object readContents(String fileName) throws IOException {
        Optional<TextFile> file = textFileRepository.findByName(fileName);

        if (file.isPresent()) {
            return file.get().readSafe();
        }

        return null;
    }

    @Override
    public List<LineMatchResult> search(String regex, String fileName) throws IOException, PatternSyntaxException {
        Pattern pattern = Pattern.compile(regex);
        List<LineMatchResult> results = new ArrayList<>();

        Optional<TextFile> file = textFileRepository.findByName(fileName);

        if (file.isPresent()) {
            results = getLineMatchResults(file.get(), pattern);
        }

        return results;
    }

    @Override
    public List<FileSearchResult> massiveSearch(String regex) throws IOException, PatternSyntaxException {
        Pattern pattern = Pattern.compile(regex);
        List<FileSearchResult> results = new ArrayList<>();
        List<TextFile> filesInThisDirectory = textFileRepository.findAll();

        filesInThisDirectory.forEach(textFile -> {
            try {
                Stream<String> lines = textFile.getContentFileContentStream();
                List<LineMatchResult> matches = new ArrayList<>();
                final int[] lineNumber = {0};

                lines.forEach(line -> {
                    Matcher matcher = pattern.matcher(line);
                    List<MatchSpan> spans = new ArrayList<>();

                    while (matcher.find()) {
                        spans.add(new MatchSpan(matcher.start(), matcher.end()));
                    }

                    if (!spans.isEmpty()) {
                        matches.add(new LineMatchResult(lineNumber[0], line, spans));
                    }

                    lineNumber[0]++;
                });

                if (!matches.isEmpty()) {
                    results.add(new FileSearchResult(textFile.getPath(), matches));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return results;
    }

    @Override
    public int replaceInFile(String regex, String replacement, String fileName) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        int[] totalReplacements = {0};
        Optional<TextFile> textFile = textFileRepository.findByName(fileName);

        if (textFile.isPresent()) {
            Path tempFile = Files.createTempFile("EDITING_TMP-", ".tmp");
            Stream<String> lines = textFile.get().getContentFileContentStream();

            BufferedWriter writer = Files.newBufferedWriter(tempFile);

            lines.forEach(line -> {
                Matcher matcher = pattern.matcher(line);
                String replaceLine = matcher.replaceAll(replacement);

                try {
                    writer.write(replaceLine);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                matcher.reset();
                while (matcher.find()) {
                    totalReplacements[0]++;
                }

            });

            Files.move(tempFile, textFile.get().getPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return totalReplacements[0];
    }

    @Override
    public void saveToFile(String fileName, List<String> lines) throws IOException {
        Optional<TextFile> textFile = textFileRepository.findByName(fileName);
        if (textFile.isPresent()) {
            try (BufferedWriter writer = Files.newBufferedWriter(textFile.get().getPath())) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    @Override
    public void createFile(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createFile(path);
            textFileRepository.save(path);
        } else {
            throw new FileAlreadyExistsException(path.toString());
        }
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        Optional<TextFile> textFile = textFileRepository.findByName(fileName);

        if (textFile.isPresent()) {
            if (Files.deleteIfExists(textFile.get().getPath())) {
                textFileRepository.delete(textFile.get().getName());
                return true;
            }
        }

        return false;
    }

    @Override
    public void syncFiles(Path dir) throws IOException {
        textFileRepository.syncFromDirectory(dir);
    }

    @Override
    public List<TextFile> getFilesInDirectory() {
        return textFileRepository.findAll();
    }

    private List<LineMatchResult> getLineMatchResults(TextFile textFile, Pattern pattern) throws IOException {
        Stream<String> lines = textFile.getContentFileContentStream();
        List<LineMatchResult> matches = new ArrayList<>();
        final int[] lineNumber = {0};

        lines.forEach(line -> {
            Matcher matcher = pattern.matcher(line);
            List<MatchSpan> spans = new ArrayList<>();

            while (matcher.find()) {
                spans.add(new MatchSpan(matcher.start(), matcher.end()));
            }

            if (!spans.isEmpty()) {
                matches.add(new LineMatchResult(lineNumber[0], line, spans));
            }

            lineNumber[0]++;
        });
        return matches;
    }
}
