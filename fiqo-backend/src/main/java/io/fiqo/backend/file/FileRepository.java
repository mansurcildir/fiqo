package io.fiqo.backend.file;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
  @NotNull
  List<File> findAllByPathStartingWith(@NotNull String path);

  @NotNull
  Optional<File> findByPathAndUserUuid(@NotNull String path, @NotNull UUID userId);

  void deleteByPathAndUserUuid(@NotNull String path, @NotNull UUID userId);

  void deleteAllByPathStartingWithAndUserUuid(@NotNull String path, @NotNull UUID userId);
}
