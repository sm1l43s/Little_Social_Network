package ru.astondevs.socialnetwork.userprofileservice.services;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.astondevs.socialnetwork.userprofileservice.exception.CustomMinioException;
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

    @Value("${minio.default-avatar-name}")
    private String defaultAvatarName;

    @Getter
    @Value("${minio.default-avatar-url}")
    private String defaultAvatarUrl;

    private final MinioClient minioClient;

    @PostConstruct
    public void init() {
        createBucketIfNotExists();
        uploadDefaultAvatar();
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

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
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

    private void uploadDefaultAvatar() {
        try {
            if (!defaultAvatarUploaded()) {
                var avatarInputStream = new ClassPathResource("static/" + defaultAvatarName).getInputStream();

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(defaultAvatarName)
                        .stream(avatarInputStream, avatarInputStream.available(), -1)
                        .contentType(MediaType.IMAGE_PNG_VALUE)
                        .build()
                );

                log.info("Default avatar has been uploaded to MinIo");

            } else {
                log.info("Default avatar already exist in MinIo");
            }
        } catch (Exception exception) {
            throw new CustomMinioException("An error occurred while loading the default avatar", exception);
        }
    }

    private boolean defaultAvatarUploaded() {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(defaultAvatarName)
                    .build());

        } catch (ErrorResponseException exception) {
            if (exception.getMessage().equals("Object does not exist")) {
                return false;
            }
        } catch (Exception exception) {
            throw new CustomMinioException("An error occurred while trying to find the default avatar", exception);
        }

        return true;
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
        if (isDefaultAvatar(fileUrl)) {
            return;
        }

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

    private boolean isDefaultAvatar(String fileUrl) {
        return defaultAvatarUrl.equals(fileUrl);
    }
}
