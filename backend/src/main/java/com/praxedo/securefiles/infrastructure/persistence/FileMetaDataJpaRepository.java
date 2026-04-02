package com.praxedo.securefiles.infrastructure.persistence;

import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetaDataJpaRepository extends JpaRepository<FileMetaData, Long>, FileMetaDataRepository {
    @NonNull Optional<FileMetaData> findById(Long id);
}
