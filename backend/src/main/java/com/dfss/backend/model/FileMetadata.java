package com.dfss.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    private String fileId;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private long size;
    private String storageProvider;
    private String storagePath;
    private LocalDateTime uploadedAt;

    public FileMetadata() {
    }

    public FileMetadata(
            String fileId,
            String originalFileName,
            String storedFileName,
            String contentType,
            long size,
            String storageProvider,
            String storagePath,
            LocalDateTime uploadedAt
    ) {
        this.fileId = fileId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.size = size;
        this.storageProvider = storageProvider;
        this.storagePath = storagePath;
        this.uploadedAt = uploadedAt;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}