package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class Result {
  private final boolean success;
  private final int status;
  private final @NotNull String messageId;
  private final @NotNull String message;

  public Result(
      final boolean success,
      final int status,
      final @NotNull String messageId,
      final @NotNull String message) {
    this.success = success;
    this.status = status;
    this.messageId = messageId;
    this.message = message;
  }
}
