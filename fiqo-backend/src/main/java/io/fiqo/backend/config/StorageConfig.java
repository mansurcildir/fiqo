package io.fiqo.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

  @Value("${storage.minio.base-url}")
  private String baseUrl;

  @Value("${storage.minio.credentials.access-key}")
  private String accessKey;

  @Value("${storage.minio.credentials.secret-key}")
  private String secretKey;

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder()
        .endpoint(this.baseUrl)
        .credentials(this.accessKey, this.secretKey)
        .build();
  }
}
