package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordForm(
    @NotNull @NotEmpty String password, @NotNull @NotEmpty String confirmPassword) {}
