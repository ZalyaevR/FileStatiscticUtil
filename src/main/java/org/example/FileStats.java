package org.example;

import lombok.Data;

import java.util.Objects;

@Data
public class FileStats {

    private final String extension;
    private int fileCount;
    private long totalSize;
    private int totalLines;
    private int nonEmptyLines;
    private int commentLines;

    public FileStats(String extension) {
        this.extension = extension;
    }
    public FileStats(String extension, int fileCount, long totalSize, int totalLines, int nonEmptyLines, int commentLines) {
        this.extension = extension;
        this.fileCount = fileCount;
        this.totalSize = totalSize;
        this.totalLines = totalLines;
        this.nonEmptyLines = nonEmptyLines;
        this.commentLines = commentLines;
    }

    public String getExtension() {
        return extension;
    }

    public void merge(FileStats other) {
        if (Objects.equals(this.extension, other.extension)) {
            this.fileCount += other.fileCount;
            this.totalSize += other.totalSize;
            this.totalLines += other.totalLines;
            this.nonEmptyLines += other.nonEmptyLines;
            this.commentLines += other.commentLines;
        }
    }

    public void incrementFilesCount() {
        this.fileCount++;
    }

    public void incrementLinesCount() {
        this.totalLines++;
    }

    public void incrementNonEmptyLinesCount() {
        this.nonEmptyLines++;
    }

    public void incrementCommentLinesCount() {
        this.commentLines++;
    }

    public void addFileSize(long size) {
        this.totalSize += size;
    }
}
