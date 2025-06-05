package io.fiqo.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ErrorResult<T> extends Result {
  public ErrorResult(final int status, final @NotNull String message) {
    super(false, status, message);
  }
}
