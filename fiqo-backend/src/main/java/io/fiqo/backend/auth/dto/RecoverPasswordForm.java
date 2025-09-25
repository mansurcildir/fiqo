package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RecoverPasswordForm(
    @NotNull @NotEmpty String password,
    @NotNull @NotEmpty String confirmPassword,
    @NotNull @NotEmpty String code) {}
