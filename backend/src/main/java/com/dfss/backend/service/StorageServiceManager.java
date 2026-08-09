package com.dfss.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class StorageServiceManager {

    private final Map<String, StorageService> providers;
    private final String activeProvider;

    public StorageServiceManager(
            List<StorageService> storageServices,
            @Value("${storage.provider:local}") String activeProvider
    ) {
        this.providers = new HashMap<>();

        for (StorageService storageService : storageServices) {
            providers.put(
                    storageService.getProviderName()
                            .toUpperCase(Locale.ROOT),
                    storageService
            );
        }

        this.activeProvider =
                activeProvider.toUpperCase(Locale.ROOT);
    }

    public StorageService getActiveStorageService() {
        return getStorageService(activeProvider);
    }

    public StorageService getStorageService(String provider) {

        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException(
                    "Storage provider is required"
            );
        }

        StorageService storageService =
                providers.get(
                        provider.toUpperCase(Locale.ROOT)
                );

        if (storageService == null) {
            throw new IllegalStateException(
                    "Storage provider not available: "
                            + provider
            );
        }

        return storageService;
    }
}