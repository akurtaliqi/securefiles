package com.praxedo.securefiles.application;

import java.io.InputStream;

public record DownloadResult(String filename, String contentType, long size, InputStream content) {}

