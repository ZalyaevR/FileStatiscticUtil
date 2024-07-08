package org.example.Formatter;

import org.example.FileStats;
import org.example.Formatter.OutputFormatter;

import java.util.Map;

public class PlainOutputFormatter implements OutputFormatter {
    @Override
    public void format(Map<String, FileStats> stats) {
        for (Map.Entry<String, FileStats> entry : stats.entrySet()) {
            FileStats stat = entry.getValue();
            System.out.printf("Extension: %s, Files: %d, Size: %d bytes, Total lines: %d, Non-empty lines: %d, Comment lines: %d%n",
                    stat.getExtension(), stat.getFileCount(), stat.getTotalSize(), stat.getTotalLines(), stat.getNonEmptyLines(), stat.getCommentLines());
        }
    }
}