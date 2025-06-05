package io.fiqo.backend.repository;

import io.fiqo.backend.data.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  @NotNull
  Optional<User> findByUsernameAndDeletedFalse(@NotNull String username);

  @NotNull
  Optional<User> findByUuidAndDeletedFalse(@NotNull UUID uuid);
}
