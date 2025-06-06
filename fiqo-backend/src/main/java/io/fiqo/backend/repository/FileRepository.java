package io.fiqo.backend.repository;

import io.fiqo.backend.data.entity.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
  @NotNull
  List<File> findAllByPathStartingWithAndDeletedFalse(@NotNull String path);

  @NotNull
  Optional<File> findByPathAndUserUuidAndDeletedFalse(@NotNull String path, @NotNull UUID userId);

  void deleteByPathAndUserUuidAndDeletedFalse(@NotNull String path, @NotNull UUID userId);

  void deleteAllByPathStartingWithAndUserUuidAndDeletedFalse(
      @NotNull String path, @NotNull UUID userId);
}
