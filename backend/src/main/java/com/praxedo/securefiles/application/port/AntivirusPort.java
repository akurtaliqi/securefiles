package com.praxedo.securefiles.application.port;

import java.io.InputStream;

public interface AntivirusPort {
    boolean scan(InputStream content);
}

