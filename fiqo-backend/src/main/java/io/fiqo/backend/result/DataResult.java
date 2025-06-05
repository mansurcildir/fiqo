package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class DataResult<T> extends Result {

  private T data;

  public DataResult(
      final boolean success,
      final int status,
      final @NotNull String message,
      final @NotNull T data) {
    super(success, status, message);
    this.data = data;
  }
}
