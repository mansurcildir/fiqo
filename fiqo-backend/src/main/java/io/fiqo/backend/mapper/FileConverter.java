package io.fiqo.backend.mapper;

import io.fiqo.backend.data.dto.file.FileInfo;
import io.fiqo.backend.data.entity.File;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileConverter {
  FileInfo toFileInfo(@NotNull File file);
}
