package com.dfss.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService implements StorageService {

    private final Path uploadDirectory;

    public LocalStorageService(
            @Value("${file.upload-dir:uploads}") String uploadDirectory
    ) throws IOException {

        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.uploadDirectory);
    }

    @Override
    public String store(
            MultipartFile file,
            String storedFileName
    ) throws IOException {

        Path destination = uploadDirectory
                .resolve(storedFileName)
                .normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.toString();
    }

    @Override
    public Resource load(String storedFileName)
            throws IOException {

        Path filePath = resolveSafePath(storedFileName);

        if (!Files.exists(filePath)
                || !Files.isRegularFile(filePath)) {
            throw new FileNotFoundException("Physical file not found");
        }

        try {
            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.isReadable()) {
                throw new FileNotFoundException(
                        "File is not readable"
                );
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new IOException(
                    "Invalid file path",
                    exception
            );
        }
    }

    @Override
    public void delete(String storedFileName)
            throws IOException {

        Path filePath = resolveSafePath(storedFileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException(
                    "Physical file not found"
            );
        }

        Files.delete(filePath);
    }

    @Override
    public boolean exists(String storedFileName) {

        try {
            Path filePath = resolveSafePath(storedFileName);

            return Files.exists(filePath)
                    && Files.isRegularFile(filePath);

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "LOCAL";
    }

    private Path resolveSafePath(String storedFileName) {

        String safeFileName = Path.of(storedFileName)
                .getFileName()
                .toString();

        if (!safeFileName.equals(storedFileName)) {
            throw new IllegalArgumentException(
                    "Invalid file name"
            );
        }

        Path filePath = uploadDirectory
                .resolve(safeFileName)
                .normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid file path"
            );
        }

        return filePath;
    }
}