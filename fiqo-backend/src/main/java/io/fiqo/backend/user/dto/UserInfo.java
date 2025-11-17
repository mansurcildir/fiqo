package io.fiqo.backend.user.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
public record UserInfo(
    @NotNull UUID uuid,
    @NotNull String username,
    @NotNull String email,
    @Nullable String firstName,
    @Nullable String lastName,
    @Nullable String phone,
    @Nullable String bio,
    @Nullable String facebookUrl,
    @Nullable String xUrl,
    @Nullable String linkedinUrl,
    @Nullable String instagramUrl,
    long totalFileSize,
    long maxFileSize,
    @NotNull List<String> roles) {}
