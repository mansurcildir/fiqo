package io.fiqo.backend.storage;

import io.fiqo.backend.file.dto.FileInfo;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface StorageStrategy {
  void upload(@NotNull String path, byte[] bytes) throws Exception;

  byte[] download(@NotNull String path) throws Exception;

  void removeFile(@NotNull String path) throws Exception;

  void removeFiles(@NotNull String path) throws Exception;

  List<FileInfo> list(@NotNull String path) throws Exception;

  void copyFile(@NotNull String sourcePath, @NotNull String targetPath) throws Exception;
}
