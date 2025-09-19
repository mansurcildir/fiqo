package io.fiqo.backend.file;

import io.fiqo.backend.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "uuid", unique = true, nullable = false, updatable = false)
  private UUID uuid;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "extension", nullable = false)
  private String extension;

  @Column(name = "path", nullable = false)
  private String path;

  @Column(name = "digest", nullable = false)
  private String digest;

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
