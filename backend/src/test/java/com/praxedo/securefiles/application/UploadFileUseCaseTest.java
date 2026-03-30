package com.praxedo.securefiles.application;

import com.praxedo.securefiles.application.port.AntivirusPort;
import com.praxedo.securefiles.application.port.FileStoragePort;
import com.praxedo.securefiles.domain.FileMetaData;
import com.praxedo.securefiles.domain.FileMetaDataRepository;
import com.praxedo.securefiles.domain.FileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UploadFileUseCaseTest {

    @Mock
    private FileMetaDataRepository fileMetaDataRepository;

    @Mock
    private FileStoragePort fileStoragePort;

    @Mock
    private AntivirusPort antivirusPort;

    @Mock
    private MultipartFile multipartFile;

    private UploadFileUseCase uploadFileUseCase;

    @BeforeEach
    void setUp() {
        uploadFileUseCase = new UploadFileUseCase(
                fileMetaDataRepository,
                fileStoragePort,
                antivirusPort
        );
    }

    @Test
    void testUploadFile_ShouldSaveFileMetadataWithUploadedStatus() throws Exception {
        String filename = "test-document.txt";
        String contentType = "text/plain";
        long fileSize = 1024L;

        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(filename);
        lenient().when(multipartFile.getContentType()).thenReturn(contentType);
        lenient().when(multipartFile.getSize()).thenReturn(fileSize);
        lenient().when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");
        
        when(antivirusPort.scan(any(InputStream.class))).thenReturn(true);

        FileMetaData savedMetaData = new FileMetaData();
        savedMetaData.setId(1L);
        savedMetaData.setOriginalFilename(filename);
        savedMetaData.setStatus(FileStatus.UPLOADED);
        savedMetaData.setFileSize(fileSize);
        savedMetaData.setContentType(contentType);

        when(fileMetaDataRepository.save(any(FileMetaData.class))).thenReturn(savedMetaData);

        FileMetaData result = uploadFileUseCase.execute(filename, multipartFile);

        assertNotNull(result);
        assertEquals(filename, result.getOriginalFilename());
        assertEquals(FileStatus.UPLOADED, result.getStatus());
        assertEquals(fileSize, result.getFileSize());
        assertEquals(contentType, result.getContentType());

        verify(antivirusPort, times(1)).scan(any(InputStream.class));
        verify(fileMetaDataRepository, times(1)).save(any(FileMetaData.class));
        verify(fileStoragePort, times(1)).store(anyString(), any(InputStream.class), eq(fileSize));
    }

    @Test
    void testUploadFile_ShouldCallAntivirusScanBeforeStoring() throws Exception {
        String filename = "malware-test.txt";
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(filename);
        lenient().when(multipartFile.getContentType()).thenReturn("text/plain");
        lenient().when(multipartFile.getSize()).thenReturn(4L);
        lenient().when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");
        
        when(antivirusPort.scan(any(InputStream.class))).thenReturn(true);

        FileMetaData savedMetaData = new FileMetaData();
        savedMetaData.setId(1L);
        savedMetaData.setStatus(FileStatus.UPLOADED);

        when(fileMetaDataRepository.save(any(FileMetaData.class))).thenReturn(savedMetaData);

        uploadFileUseCase.execute(filename, multipartFile);

        InOrder inOrder = inOrder(antivirusPort, fileMetaDataRepository, fileStoragePort);
        inOrder.verify(antivirusPort).scan(any(InputStream.class));
        inOrder.verify(fileMetaDataRepository).save(any(FileMetaData.class));
        inOrder.verify(fileStoragePort).store(anyString(), any(InputStream.class), eq(4L));
    }

    @Test
    void testUploadFile_ShouldPreserveOriginalFilename() throws Exception {
        String originalFilename = "rapport-2026.pdf";
        InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        lenient().when(multipartFile.getContentType()).thenReturn("application/pdf");
        lenient().when(multipartFile.getSize()).thenReturn(100L);
        lenient().when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");
        
        when(antivirusPort.scan(any(InputStream.class))).thenReturn(true);

        FileMetaData savedMetaData = new FileMetaData();
        savedMetaData.setOriginalFilename(originalFilename);
        savedMetaData.setStatus(FileStatus.UPLOADED);

        when(fileMetaDataRepository.save(any(FileMetaData.class))).thenReturn(savedMetaData);

        FileMetaData result = uploadFileUseCase.execute(originalFilename, multipartFile);

        assertEquals(originalFilename, result.getOriginalFilename());
    }

    @Test
    void testUploadFile_ShouldGenerateUniqueStoragePath() throws Exception {
        String filename = "test.txt";
        MultipartFile file1 = mock(MultipartFile.class);
        InputStream inputStream1 = new ByteArrayInputStream("test".getBytes());

        lenient().when(file1.getOriginalFilename()).thenReturn(filename);
        when(file1.getContentType()).thenReturn("text/plain");
        when(file1.getSize()).thenReturn(4L);
        when(file1.getInputStream()).thenReturn(inputStream1);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");

        when(antivirusPort.scan(any(InputStream.class))).thenReturn(true);

        FileMetaData savedMetaData = new FileMetaData();
        savedMetaData.setStatus(FileStatus.UPLOADED);

        when(fileMetaDataRepository.save(any(FileMetaData.class))).thenReturn(savedMetaData);

        MultipartFile file2 = mock(MultipartFile.class);
        InputStream inputStream2 = new ByteArrayInputStream("test".getBytes());

        lenient().when(file2.getOriginalFilename()).thenReturn(filename);
        when(file2.getContentType()).thenReturn("text/plain");
        when(file2.getSize()).thenReturn(4L);
        when(file2.getInputStream()).thenReturn(inputStream2);

        uploadFileUseCase.execute(filename, file1);
        uploadFileUseCase.execute(filename, file2);

        verify(fileStoragePort, times(2)).store(anyString(), any(InputStream.class), eq(4L));
    }

    @Test
    void testUploadFile_ShouldThrowExceptionOnIOError() throws Exception {
        String filename = "test.txt";

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(filename);
        lenient().when(multipartFile.getContentType()).thenReturn("text/plain");
        lenient().when(multipartFile.getSize()).thenReturn(4L);
        when(multipartFile.getInputStream()).thenThrow(new java.io.IOException("Stream error"));
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");

        assertThrows(RuntimeException.class, () -> uploadFileUseCase.execute(filename, multipartFile));
    }

    @Test
    void testUploadFile_ShouldNotSaveMetadataIfFileIsInfected() throws Exception {
        String filename = "malware.exe";
        InputStream inputStream = new ByteArrayInputStream("infected content".getBytes());

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(filename);
        lenient().when(multipartFile.getContentType()).thenReturn("application/octet-stream");
        lenient().when(multipartFile.getSize()).thenReturn(16L);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");

        when(antivirusPort.scan(any(InputStream.class))).thenReturn(false);

        assertThrows(RuntimeException.class, () -> uploadFileUseCase.execute(filename, multipartFile));

        verify(fileMetaDataRepository, never()).save(any(FileMetaData.class));
        verify(fileStoragePort, never()).store(anyString(), any(InputStream.class), anyLong());
    }

    @Test
    void testUploadFile_ShouldSaveMetadataOnlyIfFileIsSafe() throws Exception {
        String filename = "safe-document.txt";
        InputStream inputStream = new ByteArrayInputStream("safe content".getBytes());

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(filename);
        lenient().when(multipartFile.getContentType()).thenReturn("text/plain");
        lenient().when(multipartFile.getSize()).thenReturn(12L);
        lenient().when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileStoragePort.getStorageBasePath()).thenReturn("/uploads");

        when(antivirusPort.scan(any(InputStream.class))).thenReturn(true);

        FileMetaData savedMetaData = new FileMetaData();
        savedMetaData.setId(1L);
        savedMetaData.setOriginalFilename(filename);
        savedMetaData.setStatus(FileStatus.UPLOADED);

        when(fileMetaDataRepository.save(any(FileMetaData.class))).thenReturn(savedMetaData);

        FileMetaData result = uploadFileUseCase.execute(filename, multipartFile);

        assertNotNull(result);
        assertEquals(FileStatus.UPLOADED, result.getStatus());

        verify(fileMetaDataRepository, times(1)).save(any(FileMetaData.class));
        verify(fileStoragePort, times(1)).store(anyString(), any(InputStream.class), eq(12L));
    }
}

