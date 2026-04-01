package com.praxedo.securefiles.application.port;

import java.io.InputStream;
import java.util.List;

public interface FileStoragePort {
    void store(String storagePath, InputStream content, long size);
    InputStream load(String storagePath);
    String getStorageBasePath();
    List<String> listStoredFiles();
}

