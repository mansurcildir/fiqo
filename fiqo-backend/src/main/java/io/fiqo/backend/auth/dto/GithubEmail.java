package io.fiqo.backend.auth.dto;

import org.jetbrains.annotations.NotNull;

public record GithubEmail(@NotNull String email, boolean primary, boolean verified) {}
