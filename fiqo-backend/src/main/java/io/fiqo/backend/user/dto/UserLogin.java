package io.fiqo.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLogin(@NotBlank String username, @NotBlank String password) {}
