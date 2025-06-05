package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SuccessDataResult<T> extends DataResult<T> {
  public SuccessDataResult(final int status, final @NotNull String message, final @NotNull T data) {
    super(true, status, message, data);
  }
}
