package io.fiqo.backend.storage;

import io.fiqo.backend.data.dto.file.FileInfo;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MinioStrategy implements StorageStrategy {
  private final @NotNull MinioClient minioClient;

  @Value("${storage.minio.bucket}")
  private String bucket;

  public MinioStrategy(final @NotNull MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public void upload(final @NotNull String path, final byte[] bytes) throws Exception {
    try (final InputStream inputStream = new ByteArrayInputStream(bytes)) {
      this.minioClient.putObject(
          PutObjectArgs.builder().bucket(this.bucket).object(path).stream(
                  inputStream, bytes.length, -1)
              .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
              .build());
    }
  }

  @Override
  public byte[] download(final @NotNull String path) throws Exception {
    try (final InputStream stream =
        this.minioClient.getObject(
            GetObjectArgs.builder().bucket(this.bucket).object(path).build())) {
      return stream.readAllBytes();
    }
  }

  @Override
  public void remove(final @NotNull String path, final boolean recursive) throws Exception {

    if (!recursive) {
      this.minioClient.removeObject(
          RemoveObjectArgs.builder().bucket(this.bucket).object(path).build());
      return;
    }

    final List<DeleteObject> objs =
        this.list(path).stream().map(file -> new DeleteObject(file.getPath())).toList();

    final Iterable<Result<DeleteError>> results =
        this.minioClient.removeObjects(
            RemoveObjectsArgs.builder().bucket(this.bucket).objects(objs).build());

    for (final Result<DeleteError> result : results) {
      result.get();
    }
  }

  @Override
  public @NotNull List<FileInfo> list(final @NotNull String path) throws Exception {
    final List<FileInfo> files = new ArrayList<>();

    try {
      return this.listFile(path, files);
    } catch (final Exception ignored) {
    }

    return this.listFiles(path, files);
  }

  private List<FileInfo> listFile(final @NotNull String path, final @NotNull List<FileInfo> files)
      throws Exception {

    this.minioClient.statObject(StatObjectArgs.builder().bucket(this.bucket).object(path).build());

    files.add(
        FileInfo.builder()
            .name(FilenameUtils.getName(path))
            .path(path)
            .extension(FilenameUtils.getExtension(path))
            .build());

    return files;
  }

  private List<FileInfo> listFiles(final @NotNull String path, final @NotNull List<FileInfo> files)
      throws Exception {

    final Iterable<Result<Item>> results =
        this.minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(this.bucket)
                .prefix(path.endsWith("/") ? path : path + "/")
                .recursive(true)
                .build());

    for (final Result<Item> item : results) {
      final String filePath = item.get().objectName();
      final String filename = filePath.substring(path.length() + 1);
      files.add(
          FileInfo.builder()
              .name(filename)
              .path(filePath)
              .extension(FilenameUtils.getExtension(filePath))
              .build());
    }
    return files;
  }
}
