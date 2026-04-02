package com.praxedo.securefiles.infrastructure.storage;

import com.praxedo.securefiles.application.StoredFileInfo;
import com.praxedo.securefiles.application.port.FileStoragePort;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MinioFileStorageService implements FileStoragePort {

    private static final String METADATA_ORIGINAL_FILENAME = "original-filename";

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioFileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created", bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket '{}'", bucketName, e);
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    @Override
    public void store(String objectKey, InputStream content, long size, String originalFilename, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(content, size, (long) -1)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .userMetadata(Map.of(METADATA_ORIGINAL_FILENAME, originalFilename))
                    .build());
            log.info("Stored object '{}' in bucket '{}'", objectKey, bucketName);
        } catch (Exception e) {
            log.error("Failed to store object '{}'", objectKey, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public InputStream load(String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.error("Failed to load object '{}'", objectKey, e);
            throw new RuntimeException("Failed to load file", e);
        }
    }

    @Override
    public List<String> listStoredFiles() {
        try {
            List<String> keys = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
            for (Result<Item> result : results) {
                keys.add(result.get().objectName());
            }
            return keys;
        } catch (Exception e) {
            log.error("Failed to list objects in bucket '{}'", bucketName, e);
            throw new RuntimeException("Failed to list stored files", e);
        }
    }

    @Override
    public StoredFileInfo getFileMetadata(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
            String metadataValue = stat.userMetadata().get(METADATA_ORIGINAL_FILENAME).toString();
            String originalFilename = (metadataValue != null && !metadataValue.isEmpty())
                    ? metadataValue
                    : extractFilenameFromKey(objectKey);
            return new StoredFileInfo(objectKey, originalFilename, stat.contentType(), stat.size());
        } catch (Exception e) {
            log.error("Failed to get metadata for object '{}'", objectKey, e);
            throw new RuntimeException("Failed to get file metadata", e);
        }
    }

    private String extractFilenameFromKey(String objectKey) {
        int slashIndex = objectKey.indexOf('/');
        return slashIndex >= 0 ? objectKey.substring(slashIndex + 1) : objectKey;
    }
}
