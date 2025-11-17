package io.fiqo.backend.notification;

import io.fiqo.backend.notification.dto.NotificationType;
import io.fiqo.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private UUID uuid;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "message", nullable = false)
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type;

  @Column(name = "read", nullable = false)
  private boolean read;

  @Column(name = "deleted", nullable = false)
  private boolean deleted;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
