package io.fiqo.backend.exception;

import org.jetbrains.annotations.NotNull;

public class ForbiddenException extends RuntimeException {

  private static final @NotNull String DEFAULT_MESSAGE = "Forbidden";

  public ForbiddenException() {
    super(DEFAULT_MESSAGE);
  }

  public ForbiddenException(final @NotNull String message) {
    super(message);
  }
}
