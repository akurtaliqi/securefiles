package com.praxedo.securefiles.infrastructure.storage;

import com.praxedo.securefiles.application.port.FileStoragePort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements FileStoragePort {

    @Value("${file.storage.path:./backend/uploads}")
    private String storageBasePath;

    private String absoluteStoragePath;

    @PostConstruct
    public void initializeStorageDirectory() {
        try {
            Path uploadDir = Paths.get(storageBasePath).toAbsolutePath().normalize();
            absoluteStoragePath = uploadDir.toString();
            Files.createDirectories(uploadDir);
            log.info("Storage directory initialized at: {}", absoluteStoragePath);
        } catch (IOException e) {
            log.error("Failed to initialize storage directory: {}", storageBasePath, e);
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }

    @Override
    public void store(String storagePath, InputStream content, long size) {
        try {
            Path filePath = Paths.get(storagePath);
            Files.createDirectories(filePath.getParent());
            Files.copy(content, filePath);
            log.info("File stored successfully at: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to store file: {}", storagePath, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public String getStorageBasePath() {
        return absoluteStoragePath;
    }

    @Override
    public InputStream load(String storagePath) {
        try {
            Path filePath = Paths.get(storagePath);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found at path: " + storagePath);
            }
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("Failed to load file: {}", storagePath, e);
            throw new RuntimeException("Failed to load file", e);
        }
    }

    @Override
    public List<String> listStoredFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(absoluteStoragePath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)
                    .map(Path::normalize)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            log.error("Failed to list stored files in: {}", absoluteStoragePath, e);
            throw new RuntimeException("Failed to list stored files", e);
        }
    }
}

