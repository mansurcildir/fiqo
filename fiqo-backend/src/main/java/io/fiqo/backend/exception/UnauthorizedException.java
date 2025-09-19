package io.fiqo.backend.exception;

import org.jetbrains.annotations.NotNull;

public class UnauthorizedException extends RuntimeException {

  private static final @NotNull String DEFAULT_MESSAGE = "Unauthorized exception";

  public UnauthorizedException() {
    super(DEFAULT_MESSAGE);
  }

  public UnauthorizedException(final @NotNull String message) {
    super(message);
  }
}
