package org.example.Formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.FileStats;

import java.io.IOException;
import java.util.Map;

public class JsonOutputFormatter implements OutputFormatter {
    private ObjectMapper objectMapper;

    public JsonOutputFormatter() {
        this.objectMapper = new ObjectMapper();
    }

    public void format(Map<String, FileStats> stats) {
        try {
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stats);
            System.out.println(jsonOutput);
        } catch (IOException e) {
            throw new RuntimeException("Error formatting JSON output", e);
        }

    }
}