package io.fiqo.backend.data.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record AuthResponse(
    @NotNull @JsonProperty("access_token") String accessToken,
    @NotNull @JsonProperty("refresh_token") String refreshToken) {}
