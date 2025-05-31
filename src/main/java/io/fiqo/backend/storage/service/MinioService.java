package io.fiqo.backend.storage.service;

import io.fiqo.backend.storage.StorageStrategy;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService implements StorageStrategy {
  private final @NotNull MinioClient minioClient;

  @Value("${storage.minio.bucket}")
  private String bucketName;

  public MinioService(final @NotNull MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  public void uploadFile(final @NotNull String path, final @NotNull MultipartFile file)
      throws Exception {
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(path).stream(
                file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());
  }
}
