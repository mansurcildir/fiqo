package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthLogin(@NotBlank String code) {}
