package com.praxedo.securefiles.application;

import com.praxedo.securefiles.application.port.FileStoragePort;
import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import com.praxedo.securefiles.domain.FileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllFilesUseCase {

    private final FileMetaDataRepository fileMetaDataRepository;
    private final FileStoragePort fileStoragePort;

    public List<FileMetaData> execute() {
        List<String> objectKeys = fileStoragePort.listStoredFiles();
        Map<String, FileMetaData> dbFilesByPath = fileMetaDataRepository.findAll().stream()
                .collect(Collectors.toMap(FileMetaData::getStoragePath, f -> f));

        return objectKeys.stream()
                .map(key -> dbFilesByPath.containsKey(key)
                        ? dbFilesByPath.get(key)
                        : reconcileFromStorage(key))
                .collect(Collectors.toList());
    }

    private FileMetaData reconcileFromStorage(String objectKey) {
        StoredFileInfo info = fileStoragePort.getFileMetadata(objectKey);
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setOriginalFilename(info.originalFilename());
        fileMetaData.setStoragePath(objectKey);
        fileMetaData.setFileSize(info.fileSize());
        fileMetaData.setContentType(info.contentType());
        fileMetaData.setStatus(FileStatus.CLEAN);
        fileMetaData.setUploadedAt(LocalDateTime.now());
        return fileMetaDataRepository.save(fileMetaData);
    }
}
