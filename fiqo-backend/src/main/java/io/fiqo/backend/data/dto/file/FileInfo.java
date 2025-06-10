package io.fiqo.backend.data.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
@Setter
@Builder
public class FileInfo {
  private @NotNull String name;
  private @NotNull String path;
  private @NotNull String extension;
  private @NotNull String digest;
  private @JsonProperty("updated_at") Instant updatedAt;
}
