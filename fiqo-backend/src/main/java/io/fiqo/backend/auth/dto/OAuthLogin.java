package io.fiqo.backend.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

public record OAuthLogin(@NotNull @NotEmpty String code) {}
