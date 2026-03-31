package com.praxedo.securefiles.controller;

import com.praxedo.securefiles.application.UploadFileUseCase;
import com.praxedo.securefiles.application.port.AntivirusPort;
import com.praxedo.securefiles.application.port.FileStoragePort;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
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
    private AntivirusPort antivirusPort;

    @Mock
    private FileStoragePort fileStoragePort;

    @Mock
    private FileMetaDataRepository fileMetaDataRepository;

    @BeforeEach
    void setUp() {
        UploadFileUseCase uploadFileUseCase = new UploadFileUseCase(fileMetaDataRepository, fileStoragePort, antivirusPort);
        FileController fileController = new FileController(uploadFileUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
        
        lenient().when(antivirusPort.scan(any())).thenReturn(true);
        lenient().when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");
        lenient().when(fileMetaDataRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
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
                .andExpect(jsonPath("$.status").value("UPLOADED"))
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
}
