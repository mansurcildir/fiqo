package io.fiqo.backend.user.dto;

import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserDetails(@NotNull UUID userUuid, @NotNull String username) {}
