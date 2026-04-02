package com.praxedo.securefiles.application;

import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileStatus;
import com.praxedo.securefiles.infrastructure.persistence.FileMetaDataJpaRepository;
import com.praxedo.securefiles.infrastructure.storage.MinioFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final FileMetaDataJpaRepository fileMetaDataRepository;
    private final MinioFileStorageService fileStoragePort;

    public DownloadResult execute(Long id) {
        FileMetaData file = fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("File not found with id: " + id));

        if (file.getStatus() != FileStatus.CLEAN) {
            throw new IllegalStateException("File is not available for download (status: " + file.getStatus() + ")");
        }

        return new DownloadResult(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getFileSize(),
                fileStoragePort.load(file.getStoragePath())
        );
    }
}

