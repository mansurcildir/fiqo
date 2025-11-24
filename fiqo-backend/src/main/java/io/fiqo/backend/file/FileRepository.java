package io.fiqo.backend.file;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<File, Long> {
  @NotNull
  List<File> findAllByUserUuidAndPathStartingWith(@NotNull UUID userUuid, @NotNull String path);

  @NotNull
  Optional<File> findByUserUuidAndPath(@NotNull UUID userUuid, @NotNull String path);

  @Modifying
  @Query(
      """
    update File f
    set f.deleted = true,
        f.deletedAt = :deletedAt
    where f.path = :path
    and f.user.uuid = :userUuid
  """)
  void deleteByUserUuidAndPath(
      @NotNull UUID userUuid, @NotNull String path, @NotNull Instant deletedAt);

  @Modifying
  @Query(
      """
    update File f
    set f.deleted = true,
        f.deletedAt = :deletedAt
    where f.path like concat(:path, '%')
    and f.user.uuid = :userUuid
  """)
  void deleteAllByUserUuidAndPathStartingWith(
      @NotNull UUID userUuid, @NotNull String path, @NotNull Instant deletedAt);
}
