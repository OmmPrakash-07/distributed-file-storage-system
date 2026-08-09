package com.dfss.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(
            MultipartFile file,
            String storedFileName
    ) throws IOException;

    Resource load(String storedFileName)
            throws IOException;

    void delete(String storedFileName)
            throws IOException;

    boolean exists(String storedFileName);

    String getProviderName();
}