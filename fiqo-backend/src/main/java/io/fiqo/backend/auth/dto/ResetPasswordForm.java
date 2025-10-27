package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordForm(@NotBlank String password, @NotNull String confirmPassword) {}
