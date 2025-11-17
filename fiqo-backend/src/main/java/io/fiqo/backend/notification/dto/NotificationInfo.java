package io.fiqo.backend.notification.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record NotificationInfo(
    @NotNull UUID uuid,
    @NotNull String title,
    @NotNull String message,
    @NotNull NotificationType type,
    boolean read,
    @NotNull Instant createdAt) {}
