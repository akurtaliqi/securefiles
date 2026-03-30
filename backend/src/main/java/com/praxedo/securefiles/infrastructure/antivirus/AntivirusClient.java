package com.praxedo.securefiles.infrastructure.antivirus;

import com.praxedo.securefiles.application.port.AntivirusPort;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Random;

@Component
public class AntivirusClient implements AntivirusPort {

    private final Random random = new Random();

    @Override
    public boolean scan(InputStream inputStream) {
        simulateScanTime();
        return true;
    }

    private void simulateScanTime() {
        try {
            Thread.sleep(500); // simulate scan delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}