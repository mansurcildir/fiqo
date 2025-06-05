package io.fiqo.backend.exception;

import org.jetbrains.annotations.NotNull;

public class DuplicateItemException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Duplicate item";

  public DuplicateItemException() {
    super(DEFAULT_MESSAGE);
  }

  public DuplicateItemException(final @NotNull String message) {
    super(message);
  }
}
