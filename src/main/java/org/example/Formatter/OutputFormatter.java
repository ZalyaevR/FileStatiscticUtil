package org.example.Formatter;

import org.example.FileStats;

import java.util.Map;

public interface OutputFormatter {
    void format(Map<String, FileStats> stats);
}

