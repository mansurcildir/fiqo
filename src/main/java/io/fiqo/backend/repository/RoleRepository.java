package io.fiqo.backend.repository;

import io.fiqo.backend.data.entity.Role;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  @NotNull
  Optional<Role> findByName(@NotNull String name);
}
