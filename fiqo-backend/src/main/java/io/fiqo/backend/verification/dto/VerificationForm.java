package io.fiqo.backend.verification.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record VerificationForm(
    @NotNull @NotEmpty @Pattern(regexp = "\\d{6}", message = "must be 6 digits") String code) {}
