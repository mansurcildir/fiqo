package io.fiqo.backend.verification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record VerificationForm(
    @NotBlank @Pattern(regexp = "\\d{6}", message = "must be 6 digits") String code) {}
