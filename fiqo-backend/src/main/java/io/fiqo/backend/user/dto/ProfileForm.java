package io.fiqo.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProfileForm(
    @NotNull @NotEmpty @Size(max = 50) String username,
    @NotNull @NotEmpty @Email @Size(max = 100) String email) {}
