package org.example.Formatter;

public class OutputFormatterFactory {
    //TODO перенести форматы в ENUM
    public static OutputFormatter getFormatter(String format) {
        switch (format) {
            case "json":
                return new JsonOutputFormatter();
            case "xml":
                return new XmlOutputFormatter();
            case "plain":
            default:
                return new PlainOutputFormatter();
        }
    }
}