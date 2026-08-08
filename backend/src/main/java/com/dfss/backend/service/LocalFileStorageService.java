package com.dfss.backend.service;

import com.dfss.backend.dto.FileMetadataResponse;
import com.dfss.backend.dto.FileUploadResponse;
import com.dfss.backend.dto.StoredFileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.dfss.backend.model.FileMetadata;
import com.dfss.backend.repository.FileMetadataRepository;

import java.time.LocalDateTime;

@Service
public class LocalFileStorageService {

    private final Path uploadDirectory;
    private final FileMetadataRepository fileMetadataRepository;

    public LocalFileStorageService(
            @Value("${file.upload-dir:uploads}") String uploadDirectory,
            FileMetadataRepository fileMetadataRepository) throws IOException {

        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.uploadDirectory);

        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileUploadResponse store(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a file to upload");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "unknown-file";
        }

        originalFileName = Path.of(originalFileName)
                .getFileName()
                .toString();

        String fileId = UUID.randomUUID().toString();
        String extension = getExtension(originalFileName);
        String storedFileName = fileId + extension;

        Path destination = uploadDirectory
                .resolve(storedFileName)
                .normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file name");
        }

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        FileMetadata metadata = new FileMetadata(
                fileId,
                originalFileName,
                storedFileName,
                file.getContentType(),
                file.getSize(),
                "LOCAL",
                destination.toString(),
                LocalDateTime.now());

        fileMetadataRepository.save(metadata);

        return new FileUploadResponse(
                fileId,
                originalFileName,
                storedFileName,
                file.getContentType(),
                file.getSize(),
                "File uploaded successfully");
    }

    public List<StoredFileResponse> listFiles() throws IOException {

        try (Stream<Path> files = Files.list(uploadDirectory)) {

            return files
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return new StoredFileResponse(
                                    path.getFileName().toString(),
                                    Files.size(path));
                        } catch (IOException exception) {
                            throw new UncheckedIOException(exception);
                        }
                    })
                    .toList();

        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    public Resource loadFile(String storedFileName) throws IOException {

        String safeFileName = Path.of(storedFileName)
                .getFileName()
                .toString();

        if (!safeFileName.equals(storedFileName)) {
            throw new IllegalArgumentException("Invalid file name");
        }

        Path filePath = uploadDirectory
                .resolve(safeFileName)
                .normalize();

        if (!filePath.startsWith(uploadDirectory)
                || !Files.exists(filePath)
                || !Files.isRegularFile(filePath)) {

            throw new FileNotFoundException("File not found");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.isReadable()) {
                throw new FileNotFoundException("File is not readable");
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new IOException("Invalid file path", exception);
        }
    }

    public void deleteFileById(String fileId) throws IOException {

        FileMetadata metadata = fileMetadataRepository
                .findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found"));

        String storedFileName = metadata.getStoredFileName();

        Path filePath = uploadDirectory
                .resolve(storedFileName)
                .normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Physical file not found");
        }

        Files.delete(filePath);

        fileMetadataRepository.delete(metadata);
    }

    private String getExtension(String fileName) {

        int dotPosition = fileName.lastIndexOf('.');

        if (dotPosition < 0 || dotPosition == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotPosition);
    }

    public List<FileMetadataResponse> listMetadata() {

        return fileMetadataRepository.findAll()
                .stream()
                .map(metadata -> new FileMetadataResponse(
                        metadata.getFileId(),
                        metadata.getOriginalFileName(),
                        metadata.getStoredFileName(),
                        metadata.getContentType(),
                        metadata.getSize(),
                        metadata.getStorageProvider(),
                        metadata.getStoragePath(),
                        metadata.getUploadedAt()))
                .toList();
    }

    public FileMetadataResponse getMetadataById(String fileId)
            throws FileNotFoundException {

        FileMetadata metadata = fileMetadataRepository
                .findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found"));

        return new FileMetadataResponse(
                metadata.getFileId(),
                metadata.getOriginalFileName(),
                metadata.getStoredFileName(),
                metadata.getContentType(),
                metadata.getSize(),
                metadata.getStorageProvider(),
                metadata.getStoragePath(),
                metadata.getUploadedAt());
    }

    public Resource loadFileById(String fileId) throws IOException {

        FileMetadata metadata = fileMetadataRepository
                .findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found"));

        return loadFile(metadata.getStoredFileName());
    }

    public String getOriginalFileName(String fileId)
            throws FileNotFoundException {

        return fileMetadataRepository
                .findById(fileId)
                .map(FileMetadata::getOriginalFileName)
                .orElseThrow(() -> new FileNotFoundException("File metadata not found"));
    }
}