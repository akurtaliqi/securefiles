package com.praxedo.securefiles.application;

import com.praxedo.securefiles.application.port.FileStoragePort;
import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetAllFilesUseCase {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final FileStoragePort fileStoragePort;

    public List<FileMetaData> execute() {
        Set<String> storedPaths = Set.copyOf(fileStoragePort.listStoredFiles());
        return fileMetaDataRepository.findAll().stream()
                .filter(file -> storedPaths.contains(file.getStoragePath()))
                .toList();
    }
}

