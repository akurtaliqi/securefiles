package com.praxedo.securefiles.application.port;

import java.io.InputStream;

public interface FileStoragePort {
    void store(String storagePath, InputStream content, long size);
    String getStorageBasePath();
}

