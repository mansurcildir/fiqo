package io.fiqo.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProfileForm(
    @NotBlank @Size(max = 50) String username,
    @NotBlank @Email @Size(max = 100) String email,
    String firstName,
    String lastName,
    String phone,
    String bio,
    String facebookUrl,
    String xUrl,
    String linkedinUrl,
    String instagramUrl) {}
