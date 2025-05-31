package io.fiqo.backend.data.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserLogin(@NotNull @NotEmpty String username, @NotNull @NotEmpty String password) {}
