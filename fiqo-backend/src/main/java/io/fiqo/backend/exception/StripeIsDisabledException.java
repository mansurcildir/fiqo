package io.fiqo.backend.exception;

import org.jetbrains.annotations.NotNull;

public class StripeIsDisabledException extends RuntimeException {

  private static final @NotNull String DEFAULT_MESSAGE = "Stripe is disabled";

  public StripeIsDisabledException() {
    super(DEFAULT_MESSAGE);
  }

  public StripeIsDisabledException(final @NotNull String message) {
    super(message);
  }
}
