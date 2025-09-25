package io.fiqo.backend.account;

import io.fiqo.backend.account.dto.AccountItem;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {
  @NotNull
  Optional<Account> findBySubjectIdAndAccountType(
      @NotNull String username, @NotNull String providerId);

  @NotNull
  List<AccountItem> findAllByUserUuidAndAccountType(
      @NotNull UUID userUuid, @NotNull String accountType);

  boolean existsBySubjectIdAndAccountType(@NotNull String subjectId, @NotNull String accountType);

  @Modifying
  @Query(
      """
    update Account a
    set a.deleted = true,
        a.deletedAt = :deletedAt
    where a.user.uuid = :userUuid
      and a.uuid = :uuid
    """)
  void deleteByUserUuidAndUuid(
      @NotNull UUID userUuid, @NotNull UUID uuid, @NotNull Instant deletedAt);

  long countByUserUuid(@NotNull UUID userUuid);
}
