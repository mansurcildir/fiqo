package io.fiqo.backend.repository;

import io.fiqo.backend.data.entity.File;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
  @NotNull
  List<File> findAllByDeletedFalse();

  @NotNull
  Optional<File> findByPathAndUserIdAndDeletedFalse(@NotNull String path, @NotNull Long userId);

  void deleteByPathAndUserIdAndDeletedFalse(@NotNull String path, @NotNull Long userId);

  void deleteAllByPathStartingWithAndUserIdAndDeletedFalse(
      @NotNull String path, @NotNull Long userId);
}
