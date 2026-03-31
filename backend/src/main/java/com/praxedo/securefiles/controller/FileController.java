package com.praxedo.securefiles.controller;

import com.praxedo.securefiles.application.UploadFileUseCase;
import com.praxedo.securefiles.domain.FileMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;

    @PostMapping
    public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("A multipart file is required");
        }
        FileMetaData fileMetaData = uploadFileUseCase.execute(file.getOriginalFilename(), file);
        return ResponseEntity.ok(fileMetaData);
    }
}
