package io.fiqo.backend.storage;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {
  void uploadFile(final @NotNull String path, final @NotNull MultipartFile file) throws Exception;
}
