package com.praxedo.securefiles.domain;

import java.util.List;
import java.util.Optional;

public interface FileMetaDataRepository {
    FileMetaData save(FileMetaData fileMetaData);
    List<FileMetaData> findAll();
    Optional<FileMetaData> findById(Long id);
}
