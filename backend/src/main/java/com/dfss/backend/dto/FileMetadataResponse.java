package com.dfss.backend.dto;

import java.time.LocalDateTime;

public record FileMetadataResponse(
        String fileId,
        String originalFileName,
        String storedFileName,
        String contentType,
        long size,
        String storageProvider,
        String storagePath,
        LocalDateTime uploadedAt
) {
}