package com.praxedo.securefiles.application;

public record StoredFileInfo(
        String objectKey,
        String originalFilename,
        String contentType,
        long fileSize
) {}

