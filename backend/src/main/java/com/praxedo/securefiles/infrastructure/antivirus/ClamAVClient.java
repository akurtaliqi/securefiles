package com.praxedo.securefiles.infrastructure.antivirus;

import com.praxedo.securefiles.application.port.AntivirusPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
public class ClamAVClient implements AntivirusPort {

    private final String clamavHost;
    private final int clamavPort;
    private final int timeoutMillis;
    private static final int CHUNK_SIZE = 8192;

    public ClamAVClient(
            @Value("${antivirus.clamav.host:localhost}") String clamavHost,
            @Value("${antivirus.clamav.port:3310}") int clamavPort,
            @Value("${antivirus.clamav.timeout:5000}") int timeoutMillis) {
        this.clamavHost = clamavHost;
        this.clamavPort = clamavPort;
        this.timeoutMillis = timeoutMillis;
    }

    public boolean scan(InputStream inputStream) {
        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(clamavHost, clamavPort), timeoutMillis);
            socket.setSoTimeout(timeoutMillis);

            try (OutputStream out = new BufferedOutputStream(socket.getOutputStream());
                 InputStream in = new BufferedInputStream(socket.getInputStream())) {

                sendCommand(out);
                streamFile(inputStream, out);
                out.flush();

                String response = readResponse(in);
                return parseResponse(response);
            }

        } catch (IOException e) {
            throw new RuntimeException("ClamAV scan failed");
        }
    }

    private void sendCommand(OutputStream out) throws IOException {
        out.write("zINSTREAM\0".getBytes(StandardCharsets.US_ASCII));
    }

    private void streamFile(InputStream inputStream, OutputStream out) throws IOException {
        byte[] buffer = new byte[CHUNK_SIZE];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            writeChunk(out, buffer, read);
        }

        out.write(ByteBuffer.allocate(4).putInt(0).array());
    }

    private void writeChunk(OutputStream out, byte[] data, int length) throws IOException {
        byte[] size = ByteBuffer.allocate(4).putInt(length).array();

        out.write(size);
        out.write(data, 0, length);
    }

    private String readResponse(InputStream in) throws IOException {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int read;

        while ((read = in.read(buffer)) != -1) {
            responseBuffer.write(buffer, 0, read);

            if (buffer[read - 1] == 0) {
                break;
            }
        }

        return responseBuffer.toString(StandardCharsets.US_ASCII);
    }

    private boolean parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Empty response from ClamAV");
        }

        if (response.contains("FOUND")) {
            return false;
        }

        if (response.contains("OK")) {
            return true;
        }

        throw new RuntimeException("Unexpected ClamAV response: " + response);
    }
}