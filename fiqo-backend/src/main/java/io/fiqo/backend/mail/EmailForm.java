package io.fiqo.backend.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailForm(@NotBlank @Email String email) {}
