package com.praxedo.securefiles.api;

import com.praxedo.securefiles.application.DownloadFileUseCase;
import com.praxedo.securefiles.application.DownloadResult;
import com.praxedo.securefiles.application.GetAllFilesUseCase;
import com.praxedo.securefiles.application.UploadFileUseCase;
import com.praxedo.securefiles.domain.FileMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileResource {

    private final UploadFileUseCase uploadFileUseCase;
    private final GetAllFilesUseCase getAllFilesUseCase;
    private final DownloadFileUseCase downloadFileUseCase;

    @PostMapping
    public ResponseEntity<FileMetaData> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("A multipart file is required");
        }
        return ResponseEntity.ok(uploadFileUseCase.execute(file.getOriginalFilename(), file));
    }

    @GetMapping
    public ResponseEntity<List<FileMetaData>> getAllFiles() {
        return ResponseEntity.ok(getAllFilesUseCase.execute());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long id) {
        DownloadResult result = downloadFileUseCase.execute(id);
        MediaType mediaType = result.contentType() != null
                ? MediaType.parseMediaType(result.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        StreamingResponseBody stream = output -> {
            try (var input = result.content()) {
                input.transferTo(output);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(mediaType)
                .contentLength(result.size())
                .body(stream);
    }
}


