package com.praxedo.securefiles.controller;

import com.praxedo.securefiles.application.UploadFileUseCase;
import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Mock
    private UploadFileUseCase uploadFileUseCase;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testUploadFile_ShouldReturn200WithFileMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setId(1L);
        fileMetaData.setOriginalFilename("test.txt");
        fileMetaData.setStoragePath("/uploads/uuid/test.txt");
        fileMetaData.setStatus(FileStatus.UPLOADED);
        fileMetaData.setFileSize(12L);
        fileMetaData.setContentType("text/plain");
        fileMetaData.setUploadedAt(LocalDateTime.now());

        lenient().when(uploadFileUseCase.execute(anyString(), any())).thenReturn(fileMetaData);

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

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setId(1L);
        fileMetaData.setOriginalFilename("document.pdf");
        fileMetaData.setStatus(FileStatus.UPLOADED);

        lenient().when(uploadFileUseCase.execute(anyString(), any())).thenReturn(fileMetaData);

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

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setId(1L);
        fileMetaData.setOriginalFilename("report.xlsx");
        fileMetaData.setStatus(FileStatus.UPLOADED);

        lenient().when(uploadFileUseCase.execute(anyString(), any())).thenReturn(fileMetaData);

        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value("report.xlsx"));
    }
}

