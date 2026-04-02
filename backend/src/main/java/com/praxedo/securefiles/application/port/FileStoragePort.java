package com.praxedo.securefiles.application.port;

import com.praxedo.securefiles.application.StoredFileInfo;

import java.io.InputStream;
import java.util.List;

public interface FileStoragePort {
    void store(String objectKey, InputStream content, long size, String originalFilename, String contentType);
    InputStream load(String objectKey);
    List<String> listStoredFiles();
    StoredFileInfo getFileMetadata(String objectKey);
}
