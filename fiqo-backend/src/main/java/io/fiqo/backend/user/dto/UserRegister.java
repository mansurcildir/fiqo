package io.fiqo.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRegister(
    @NotBlank String username, @NotBlank String password, @NotBlank @Email String email) {}
