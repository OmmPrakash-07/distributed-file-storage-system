package com.dfss.backend.repository;

import com.dfss.backend.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, String> {

    Optional<FileMetadata> findByStoredFileName(String storedFileName);
}   