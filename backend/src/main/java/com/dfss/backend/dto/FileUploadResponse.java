package com.dfss.backend.dto;

public record FileUploadResponse(
        String fileId,
        String originalFileName,
        String storedFileName,
        String contentType,
        long size,
        String message
) {
}