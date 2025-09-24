package io.fiqo.backend.file;

import io.fiqo.backend.file.dto.FileInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileConverter {
  FileInfo toFileInfo(File file);
}
