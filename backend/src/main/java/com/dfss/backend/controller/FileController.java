package com.dfss.backend.controller;

import com.dfss.backend.dto.FileMetadataResponse;
import com.dfss.backend.dto.FileUploadResponse;
import com.dfss.backend.service.LocalFileStorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final LocalFileStorageService fileStorageService;

    public FileController(
            LocalFileStorageService fileStorageService
    ) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        FileUploadResponse response =
                fileStorageService.store(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<FileMetadataResponse>> listFiles() {

        return ResponseEntity.ok(
                fileStorageService.listMetadata()
        );
    }

    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<FileMetadataResponse> getFileMetadata(
            @PathVariable String fileId
    ) throws IOException {

        return ResponseEntity.ok(
                fileStorageService.getMetadataById(fileId)
        );
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFileById(
            @PathVariable String fileId
    ) throws IOException {

        Resource resource =
                fileStorageService.loadFileById(fileId);

        String originalFileName =
                fileStorageService.getOriginalFileName(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                originalFileName +
                                "\""
                )
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String fileId
    ) throws IOException {

        fileStorageService.deleteFileById(fileId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "File deleted successfully",
                        "fileId", fileId
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFile(
            IllegalArgumentException exception
    ) {

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", exception.getMessage()
                )
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFound(
            FileNotFoundException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "success", false,
                                "message", exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleStorageError(
            IOException exception
    ) {

        return ResponseEntity
                .internalServerError()
                .body(
                        Map.of(
                                "success", false,
                                "message", "File operation failed"
                        )
                );
    }
}