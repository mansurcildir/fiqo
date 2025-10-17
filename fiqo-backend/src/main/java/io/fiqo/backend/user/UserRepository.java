package io.fiqo.backend.user;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  @NotNull
  Optional<User> findByUsername(@NotNull String username);

  @NotNull
  Optional<User> findByEmail(@NotNull String email);

  @NotNull
  Optional<User> findByUuid(@NotNull UUID uuid);

  boolean existsByUsername(@NotNull String username);

  boolean existsByEmail(@NotNull String email);

  @Modifying
  @Query(
      "update User u set u.totalFileSize = u.totalFileSize + :totalFileSize where u.uuid = :uuid")
  void updateTotalFileSizeByUuid(@NotNull UUID uuid, long totalFileSize);
}
