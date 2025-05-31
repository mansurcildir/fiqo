package io.fiqo.backend.data.entity;

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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "REFRESH_TOKEN")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "UUID", unique = true, nullable = false, updatable = false)
  private UUID uuid;

  @Column(name = "TOKEN", unique = true, nullable = false)
  private String token;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;

  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "UPDATED_AT", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;
}
