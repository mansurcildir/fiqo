package io.fiqo.backend.verification;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
  @NotNull
  Optional<Verification> findByCode(@NotNull String code);
}
