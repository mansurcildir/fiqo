package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordForm(
    @NotBlank String password, @NotBlank String confirmPassword, @NotBlank String code) {}
