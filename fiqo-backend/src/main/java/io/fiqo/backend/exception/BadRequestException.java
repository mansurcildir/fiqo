package io.fiqo.backend.exception;

import org.jetbrains.annotations.NotNull;

public class BadRequestException extends RuntimeException {

  private static final @NotNull String DEFAULT_MESSAGE = "Bad request";

  public BadRequestException() {
    super(DEFAULT_MESSAGE);
  }

  public BadRequestException(final @NotNull String message) {
    super(message);
  }
}
