package com.dfss.backend.dto;

public record StoredFileResponse(
        String storedFileName,
        long size
) {
}