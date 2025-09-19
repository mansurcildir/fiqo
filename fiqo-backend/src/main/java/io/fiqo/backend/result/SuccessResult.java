package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SuccessResult extends Result {
  public SuccessResult(
      final int status, final @NotNull String messageId, final @NotNull String message) {
    super(true, status, messageId, message);
  }
}
