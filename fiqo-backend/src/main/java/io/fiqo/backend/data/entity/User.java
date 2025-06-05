package io.fiqo.backend.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "UUID", unique = true, nullable = false, updatable = false)
  private UUID uuid;

  @Column(name = "USERNAME", unique = true, nullable = false)
  private String username;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "EMAIL", nullable = false)
  private String email;

  @Column(name = "DELETED", nullable = false)
  private boolean deleted;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<RefreshToken> refreshTokens;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "USER_ROLE",
      joinColumns = @JoinColumn(name = "USER_ID"),
      inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @Column(name = "UPDATED_AT", nullable = false)
  @UpdateTimestamp
  private Instant updatedAt;

  @Column(name = "DELETED_AT")
  private Instant deletedAt;
}
