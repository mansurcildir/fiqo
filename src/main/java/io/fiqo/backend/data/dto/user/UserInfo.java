package io.fiqo.backend.data.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserInfo(
    @NotNull @NotEmpty UUID uuid,
    @NotNull @NotEmpty String username,
    @NotNull @NotEmpty String email,
    @NotNull @NotEmpty List<String> roles) {}
