package io.fiqo.backend.file.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
public class FileInfo {
  private @NotNull String name;
  private @NotNull String path;
  private @NotNull String extension;
  private @NotNull String digest;
  private long size;
  private @NotNull Instant createdAt;
  private @NotNull Instant updatedAt;
}
