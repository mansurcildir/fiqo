package io.fiqo.backend.notification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @NotNull
  Optional<Notification> findByUserUuidAndUuid(@NotNull UUID useruuid, @NotNull UUID uuid);

  @NotNull
  List<Notification> findAllByUserUuidOrderByCreatedAtDesc(@NotNull UUID userUuid);

  @Modifying
  @Query(
      """
        update Notification n
        set n.deleted = true,
            n.deletedAt = :deletedAt
        where n.user.uuid = :userUuid
          and n.uuid = :uuid
        """)
  void deleteByUserUuidAndUuid(
      @NotNull UUID userUuid, @NotNull UUID uuid, @NotNull Instant deletedAt);
}
