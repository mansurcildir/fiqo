package io.fiqo.backend.result;

import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseFactory {
  private final @NotNull MessageSource messageSource;

  public @NotNull Result success(final int status, final @NotNull String msgId) {
    final String message =
        Objects.requireNonNull(
            this.messageSource.getMessage(msgId, null, msgId, Locale.getDefault()));

    return new SuccessResult(status, msgId, message);
  }

  public @NotNull <T> DataResult<T> success(
      final int status, final @NotNull String msgId, final @NotNull T data) {
    final String message =
        Objects.requireNonNull(
            this.messageSource.getMessage(msgId, null, msgId, Locale.getDefault()));

    return new SuccessDataResult<>(status, msgId, message, data);
  }

  public @NotNull Result error(final int status, final @NotNull String msgId) {
    final String message =
        Objects.requireNonNull(
            this.messageSource.getMessage(msgId, null, msgId, Locale.getDefault()));

    return new ErrorResult(status, msgId, message);
  }

  public @NotNull <T> DataResult<T> error(
      final int status, final @NotNull String msgId, final @NotNull T data) {
    final String message =
        Objects.requireNonNull(
            this.messageSource.getMessage(msgId, null, msgId, Locale.getDefault()));

    return new ErrorDataResult<>(status, msgId, message, data);
  }
}
