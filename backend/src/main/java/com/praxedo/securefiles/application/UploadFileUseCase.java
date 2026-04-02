package com.praxedo.securefiles.application;

import com.praxedo.securefiles.application.port.AntivirusPort;
import com.praxedo.securefiles.application.port.FileStoragePort;
import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import com.praxedo.securefiles.domain.FileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadFileUseCase {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final FileStoragePort fileStoragePort;
    private final AntivirusPort antivirusPort;

    public FileMetaData execute(String originalFilename, MultipartFile file) {
        String objectKey = UUID.randomUUID() + "/" + originalFilename;

        scanFile(file);

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setOriginalFilename(originalFilename);
        fileMetaData.setStoragePath(objectKey);
        fileMetaData.setStatus(FileStatus.CLEAN);
        fileMetaData.setFileSize(file.getSize());
        fileMetaData.setContentType(file.getContentType());
        fileMetaData.setUploadedAt(LocalDateTime.now());

        FileMetaData savedFile = fileMetaDataRepository.save(fileMetaData);

        try {
            fileStoragePort.store(objectKey, file.getInputStream(), file.getSize(), originalFilename, file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        return savedFile;
    }

    private void scanFile(MultipartFile file) {
        try {
            boolean isFileSafe = antivirusPort.scan(file.getInputStream());
            if (!isFileSafe) {
                throw new SecurityException("File is infected and cannot be stored");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan file", e);
        }
    }
}
