package io.fiqo.backend.storage;

import io.fiqo.backend.data.dto.file.FileInfo;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface StorageStrategy {
  void upload(@NotNull String path, byte[] bytes) throws Exception;

  byte[] download(@NotNull String path) throws Exception;

  void remove(@NotNull String path, boolean recursive) throws Exception;

  List<FileInfo> list(@NotNull String path) throws Exception;
}
