package ru.astondevs.socialnetwork.userpostservice.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.astondevs.socialnetwork.userpostservice.exception.CustomMinioException;
import java.nio.file.Files;

import static java.util.UUID.randomUUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.policy-file}")
    private String policy;

    private final MinioClient minioClient;

    @PostConstruct
    public void init() {
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        try {
            var bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }

            setBucketPolicy();

        } catch (Exception exception) {
            throw new CustomMinioException("Failed to create minio bucket", exception);
        }
    }

    private void setBucketPolicy() {
        try {
            var policy = loadPolicyFromFile();

            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build()
            );

            log.info("Bucket policy applied from {}", policy);

        } catch (Exception exception) {
            throw new CustomMinioException("Failed to set bucket policy from " + policy, exception);
        }
    }

    @SneakyThrows
    private String loadPolicyFromFile() {
        var path = new ClassPathResource(policy).getFile().toPath();
        return Files.readString(path);
    }

    public String uploadFile(MultipartFile file) {
        try {
            var filename = randomUUID().toString();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return getFileUrl(filename);

        } catch (Exception exception) {
            throw new CustomMinioException("Failed to upload file", exception);
        }
    }

    private String getFileUrl(String filename) {
        return minioUrl + "/" + bucketName + "/" + filename;
    }

    public void deleteFile(String fileUrl) {
        try {
            var fileName = extractFileName(fileUrl);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception exception) {
            throw new CustomMinioException("Error when trying to delete file: ", exception);
        }
    }

    private String extractFileName(String fileUrl) {
        var fileNameStartIndex = fileUrl.lastIndexOf("/") + 1;
        return fileUrl.substring(fileNameStartIndex);
    }
}
