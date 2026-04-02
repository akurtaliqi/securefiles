package com.praxedo.securefiles.controller;

import com.praxedo.securefiles.application.DownloadFileUseCase;
import com.praxedo.securefiles.application.GetAllFilesUseCase;
import com.praxedo.securefiles.application.UploadFileUseCase;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import com.praxedo.securefiles.exception.GlobalExceptionHandler;
import com.praxedo.securefiles.infrastructure.antivirus.ClamAVClient;
import com.praxedo.securefiles.infrastructure.persistence.FileMetaDataJpaRepository;
import com.praxedo.securefiles.infrastructure.storage.MinioFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClamAVClient antivirusPort;

    @Mock
    private MinioFileStorageService fileStoragePort;

    @Mock
    private FileMetaDataJpaRepository fileMetaDataRepository;

    @BeforeEach
    void setUp() {
        UploadFileUseCase uploadFileUseCase = new UploadFileUseCase(fileMetaDataRepository, fileStoragePort, antivirusPort);
        GetAllFilesUseCase getAllFilesUseCase = new GetAllFilesUseCase(fileMetaDataRepository, fileStoragePort);
        DownloadFileUseCase downloadFileUseCase = new DownloadFileUseCase(fileMetaDataRepository, fileStoragePort);
        FileController fileController = new FileController(uploadFileUseCase, getAllFilesUseCase, downloadFileUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        lenient().when(antivirusPort.scan(any())).thenReturn(true);
    }

    @Test
    void testUploadFile_ShouldReturn200WithFileMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("test.txt"))
                .andExpect(jsonPath("$.status").value("CLEAN"))
                .andExpect(jsonPath("$.fileSize").value(12L))
                .andExpect(jsonPath("$.contentType").value("text/plain"));
    }

    @Test
    void testUploadFile_ShouldCallUseCaseWithCorrectParameters() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("document.pdf"));
    }

    @Test
    void testUploadFile_ShouldPreserveFileExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "excel content".getBytes()
        );


        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("report.xlsx"));
    }

    @Test
    void testUploadFile_ShouldRejectEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        mockMvc.perform(multipart("/api/files").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A multipart file is required"));
    }

    @Test
    void testUploadFile_ShouldRejectMissingFilePart() throws Exception {
        mockMvc.perform(multipart("/api/files"))
                .andExpect(status().isBadRequest());
    }
}
