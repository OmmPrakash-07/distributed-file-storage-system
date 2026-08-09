package com.dfss.backend.service;

import com.dfss.backend.dto.FileMetadataResponse;
import com.dfss.backend.dto.FileUploadResponse;
import com.dfss.backend.model.FileMetadata;
import com.dfss.backend.repository.FileMetadataRepository;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorageService {

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    public LocalFileStorageService(
            StorageService storageService,
            FileMetadataRepository fileMetadataRepository
    ) {
        this.storageService = storageService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileUploadResponse store(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a file to upload"
            );
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            originalFileName = "unknown-file";
        }

        originalFileName = Path.of(originalFileName)
                .getFileName()
                .toString();

        String fileId = UUID.randomUUID().toString();

        String extension =
                getExtension(originalFileName);

        String storedFileName =
                fileId + extension;

        String storagePath =
                storageService.store(
                        file,
                        storedFileName
                );

        FileMetadata metadata =
                new FileMetadata(
                        fileId,
                        originalFileName,
                        storedFileName,
                        file.getContentType(),
                        file.getSize(),
                        storageService.getProviderName(),
                        storagePath,
                        LocalDateTime.now()
                );

        fileMetadataRepository.save(metadata);

        return new FileUploadResponse(
                fileId,
                originalFileName,
                storedFileName,
                file.getContentType(),
                file.getSize(),
                "File uploaded successfully"
        );
    }

    public List<FileMetadataResponse> listMetadata() {

        return fileMetadataRepository
                .findAll()
                .stream()
                .map(metadata ->
                        new FileMetadataResponse(
                                metadata.getFileId(),
                                metadata.getOriginalFileName(),
                                metadata.getStoredFileName(),
                                metadata.getContentType(),
                                metadata.getSize(),
                                metadata.getStorageProvider(),
                                metadata.getStoragePath(),
                                metadata.getUploadedAt()
                        )
                )
                .toList();
    }

    public FileMetadataResponse getMetadataById(
            String fileId
    ) throws FileNotFoundException {

        FileMetadata metadata =
                fileMetadataRepository
                        .findById(fileId)
                        .orElseThrow(() ->
                                new FileNotFoundException(
                                        "File metadata not found"
                                )
                        );

        return new FileMetadataResponse(
                metadata.getFileId(),
                metadata.getOriginalFileName(),
                metadata.getStoredFileName(),
                metadata.getContentType(),
                metadata.getSize(),
                metadata.getStorageProvider(),
                metadata.getStoragePath(),
                metadata.getUploadedAt()
        );
    }

    public Resource loadFileById(
            String fileId
    ) throws IOException {

        FileMetadata metadata =
                fileMetadataRepository
                        .findById(fileId)
                        .orElseThrow(() ->
                                new FileNotFoundException(
                                        "File metadata not found"
                                )
                        );

        return storageService.load(
                metadata.getStoredFileName()
        );
    }

    public String getOriginalFileName(
            String fileId
    ) throws FileNotFoundException {

        return fileMetadataRepository
                .findById(fileId)
                .map(FileMetadata::getOriginalFileName)
                .orElseThrow(() ->
                        new FileNotFoundException(
                                "File metadata not found"
                        )
                );
    }

    public void deleteFileById(
            String fileId
    ) throws IOException {

        FileMetadata metadata =
                fileMetadataRepository
                        .findById(fileId)
                        .orElseThrow(() ->
                                new FileNotFoundException(
                                        "File metadata not found"
                                )
                        );

        storageService.delete(
                metadata.getStoredFileName()
        );

        fileMetadataRepository.delete(metadata);
    }

    private String getExtension(
            String fileName
    ) {

        int dotPosition =
                fileName.lastIndexOf('.');

        if (dotPosition < 0
                || dotPosition ==
                fileName.length() - 1) {

            return "";
        }

        return fileName.substring(dotPosition);
    }
}