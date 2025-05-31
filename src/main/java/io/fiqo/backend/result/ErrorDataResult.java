package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ErrorDataResult<T> extends DataResult<T> {
  public ErrorDataResult(final int status, final @NotNull String message, final @NotNull T data) {
    super(false, status, message, data);
  }
}
