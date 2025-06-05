package io.fiqo.backend.data.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileInfo {
  private String name;
  private String path;
  private String extension;
}
