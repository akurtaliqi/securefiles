package com.praxedo.securefiles.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileMetaDataTest {

    @Test
    void testFileMetaData_ShouldInitializeWithUploadedStatus() {
        FileMetaData fileMetaData = new FileMetaData();

        fileMetaData.setStatus(FileStatus.UPLOADED);

        assertEquals(FileStatus.UPLOADED, fileMetaData.getStatus());
    }

    @Test
    void testFileMetaData_ShouldStoreOriginalFilename() {
        FileMetaData fileMetaData = new FileMetaData();
        String filename = "test-document.txt";

        fileMetaData.setOriginalFilename(filename);

        assertEquals(filename, fileMetaData.getOriginalFilename());
    }

    @Test
    void testFileMetaData_ShouldStoreFileSize() {
        FileMetaData fileMetaData = new FileMetaData();
        long fileSize = 5242880L; // 5MB

        fileMetaData.setFileSize(fileSize);

        assertEquals(fileSize, fileMetaData.getFileSize());
    }

    @Test
    void testFileMetaData_ShouldStoreStoragePath() {
        FileMetaData fileMetaData = new FileMetaData();
        String storagePath = "/uploads/uuid-123/test-document.txt";

        fileMetaData.setStoragePath(storagePath);

        assertEquals(storagePath, fileMetaData.getStoragePath());
    }

    @Test
    void testFileMetaData_ShouldStoreContentType() {
        FileMetaData fileMetaData = new FileMetaData();
        String contentType = "application/pdf";

        fileMetaData.setContentType(contentType);

        assertEquals(contentType, fileMetaData.getContentType());
    }

    @Test
    void testFileMetaData_ShouldStoreUploadedTimestamp() {
        FileMetaData fileMetaData = new FileMetaData();
        LocalDateTime now = LocalDateTime.now();

        fileMetaData.setUploadedAt(now);

        assertEquals(now, fileMetaData.getUploadedAt());
    }

    @Test
    void testFileMetaData_ShouldStoreScannedTimestamp() {
        FileMetaData fileMetaData = new FileMetaData();
        LocalDateTime scannedTime = LocalDateTime.now();

        fileMetaData.setScannedAt(scannedTime);

        assertEquals(scannedTime, fileMetaData.getScannedAt());
    }

    @Test
    void testFileStatus_ShouldHaveUploadedStatus() {
        assertNotNull(FileStatus.UPLOADED);
    }

    @Test
    void testFileStatus_ShouldHaveScanningStatus() {
        assertNotNull(FileStatus.SCANNING);
    }

    @Test
    void testFileStatus_ShouldHaveCleanStatus() {
        assertNotNull(FileStatus.CLEAN);
    }

    @Test
    void testFileStatus_ShouldHaveInfectedStatus() {
        assertNotNull(FileStatus.INFECTED);
    }

    @Test
    void testFileMetaData_ShouldStoreAllPropertiesTogether() {
        FileMetaData fileMetaData = new FileMetaData();
        String filename = "important-report.xlsx";
        String storagePath = "/uploads/uuid-456/important-report.xlsx";
        long fileSize = 2097152L; // 2MB
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        LocalDateTime uploadedAt = LocalDateTime.now();

        fileMetaData.setOriginalFilename(filename);
        fileMetaData.setStoragePath(storagePath);
        fileMetaData.setStatus(FileStatus.UPLOADED);
        fileMetaData.setFileSize(fileSize);
        fileMetaData.setContentType(contentType);
        fileMetaData.setUploadedAt(uploadedAt);

        assertEquals(filename, fileMetaData.getOriginalFilename());
        assertEquals(storagePath, fileMetaData.getStoragePath());
        assertEquals(FileStatus.UPLOADED, fileMetaData.getStatus());
        assertEquals(fileSize, fileMetaData.getFileSize());
        assertEquals(contentType, fileMetaData.getContentType());
        assertEquals(uploadedAt, fileMetaData.getUploadedAt());
    }
}

