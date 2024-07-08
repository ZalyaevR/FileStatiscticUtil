package org.example;

import org.eclipse.jgit.ignore.IgnoreNode;
import org.example.Formatter.OutputFormatter;
import org.example.Formatter.OutputFormatterFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class StatisticUtilMain {
    private static IgnoreNode gitIgnore = null;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Обработка параметров командной строки
        CommandLineParameters parameters;
        ExecutorService executorService;

        parameters = CommandLineParameters.parse(args);


        if (parameters.isGitIgnore()) {
            gitIgnore = loadGitIgnore(parameters.getPath());
        }

        // Создание пула потоков
        executorService = Executors.newFixedThreadPool(parameters.getThreadCount());

        // Список задач для выполнения
        List<Callable<FileStats>> tasks = new ArrayList<>();

        Path basePath = parameters.getPath();
        int baseDepth = basePath.getNameCount();

        // Обход файловой системы
        Files.walkFileTree(parameters.getPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (shouldProcessFile(file, parameters, gitIgnore)) {
                    tasks.add(() -> processFile(file));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                int currentDepth = dir.getNameCount() - baseDepth;
                if (parameters.isRecursive() || parameters.getMaxDepth() == 0 || parameters.getMaxDepth() > currentDepth) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }
        });


        List<Future<FileStats>> results = executorService.invokeAll(tasks);
        Map<String, FileStats> stats = new HashMap<>();
        for (Future<FileStats> result : results) {
            FileStats stat = result.get();
            stats.merge(stat.getExtension(), stat, (existingStat, newStat) -> {
                existingStat.merge(newStat);
                return existingStat;
            });
        }

        executorService.shutdown();

        OutputFormatter formatter = OutputFormatterFactory.getFormatter(parameters.getOutputFormat());
        System.out.println("Result:");
        formatter.format(stats);
        if (stats.isEmpty()) {
            System.out.println("There are no files of the specified format in the specified directory");
            System.exit(1);
        }
    }

    private static boolean shouldProcessFile(Path file, CommandLineParameters parameters, IgnoreNode gitIgnore) {
        if (gitIgnore != null) {
            String relativePath = parameters.getPath().relativize(file).toString();
            if (gitIgnore.isIgnored(relativePath, false) == IgnoreNode.MatchResult.IGNORED) {
                return false;
            }
        }

        String extension = getFileExtension(file);
        if (parameters.getIncludeExtensions().isEmpty() || parameters.getIncludeExtensions().contains(extension)) {
            return !parameters.getExcludeExtensions().contains(extension);
        }
        return false;
    }

    private static String getFileExtension(Path file) {
        String name = file.toString();
        int lastIndex = name.lastIndexOf('.');
        return lastIndex == -1 ? "" : name.substring(lastIndex + 1);
    }

    private static FileStats processFile(Path file) {
        FileStats stats = new FileStats(getFileExtension(file));
        stats.incrementFilesCount();
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            processLines(stats, lines);
        } catch (MalformedInputException e) {
            try {
                List<String> lines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
                processLines(stats, lines);
            } catch (IOException ex) {
                System.err.println("Failed to process (decoding) file with ISO-8859-1 and UTF-8: " + file);
                ex.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Failed to process file: " + file);
            e.printStackTrace();
        }
        try {
            stats.addFileSize(Files.size(file));
        } catch (IOException e) {
            System.err.println("Failed to get file size: " + file);
            e.printStackTrace();
        }
        return stats;
    }

    private static void processLines(FileStats stats, List<String> lines) {
        for (String line : lines) {
            stats.incrementLinesCount();
            if (!line.trim().isEmpty()) {
                stats.incrementNonEmptyLinesCount();
            }
            if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                stats.incrementCommentLinesCount();
            }
        }
    }

    private static IgnoreNode loadGitIgnore(Path directory) throws IOException {
        Path gitIgnorePath = directory.resolve(".gitignore");
        if (Files.exists(gitIgnorePath)) {
            IgnoreNode ignoreNode = new IgnoreNode();
            List<String> lines = Files.readAllLines(gitIgnorePath, StandardCharsets.UTF_8);
            String combinedLines = String.join("\n", lines);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(combinedLines.getBytes(StandardCharsets.UTF_8))) {
                ignoreNode.parse(inputStream);
            }
            return ignoreNode;
        }
        return null;
    }
}