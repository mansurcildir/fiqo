package io.fiqo.backend.notification.dto;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record NotificationForm(
    @NotNull String title, @NotNull String message, @NotNull NotificationType type) {}
