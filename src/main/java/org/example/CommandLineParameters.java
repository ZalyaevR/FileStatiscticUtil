package org.example;

import lombok.Data;
import org.apache.commons.cli.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Data
public class CommandLineParameters {

    private final Path path;
    private final boolean recursive;
    private final int maxDepth;
    private final int threadCount;
    private final Set<String> includeExtensions;
    private final Set<String> excludeExtensions;
    private final boolean gitIgnore;
    private final String outputFormat;


    public static CommandLineParameters parse(String[] args) {
        Options options = createOptions();
        CommandLineParser clp = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = clp.parse(options, args);
            if (cmd == null || cmd.hasOption("help")) {
                printHelp(options);
                System.exit(0);
            }
            Path path = Paths.get(cmd.getOptionValue("path"));

            if (!Files.exists(path) || !Files.isDirectory(path)) {
                throw new NoSuchFileException("Path does not exist or is not a directory: " + path);
            }

            boolean recursive = cmd.hasOption("recursive");
            int maxDepth = Integer.parseInt(cmd.getOptionValue("max-depth", "0"));
            int threadCount = Math.max(1,Integer.parseInt(cmd.getOptionValue("thread", "1")));
            Set<String> includeExtensions = Set.of(cmd.getOptionValue("include-ext", "txt").split(","));
            Set<String> excludeExtensions = Set.of(cmd.getOptionValue("exclude-ext", "").split(","));
            boolean gitIgnore = cmd.hasOption("git-ignore");
            String outputFormat = cmd.getOptionValue("output", "plain");

            return new CommandLineParameters(path, recursive, maxDepth, threadCount, includeExtensions, excludeExtensions, gitIgnore, outputFormat);

        } catch (Exception e) {
            if(e.getMessage() == null){
                System.err.println("Invalid input parameters");
            } else {
                System.err.println(e.getMessage());
            }
            printHelp(options);
            System.exit(1);
        }
        return null;
    }
    private static Options createOptions() {
        Options options = new Options();
        options.addOption("p", "path", true, "Path to directory");
        options.addOption("r", "recursive", false, "Recursive");
        options.addOption("d", "max-depth", true, "Max depth");
        options.addOption("t", "thread", true, "Thread count");
        options.addOption("i", "include-ext", true, "Include extensions");
        options.addOption("e", "exclude-ext", true, "Exclude extensions");
        options.addOption("g", "git-ignore", false, "Git ignore");
        options.addOption("o", "output", true, "Output format");
        options.addOption("h", "help", false, "Print this help message");
        return options;
    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Available commands:", options);
    }
}