package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class Result {
  private boolean success;
  private int status;
  private String messageId;
  private String message;

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
