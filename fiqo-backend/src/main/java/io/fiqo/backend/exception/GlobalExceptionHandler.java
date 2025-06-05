package io.fiqo.backend.exception;

import io.fiqo.backend.result.DataResult;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final @NotNull ResponseFactory responseFactory;

  @ExceptionHandler(Exception.class)
  public @NotNull ResponseEntity<Object> handleException(final @NotNull Exception ex) {
    log.error(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public @NotNull ResponseEntity<Object> handleValidations(
      final @NotNull MethodArgumentNotValidException ex) {
    log.info(ex.getMessage());

    final var errors = new HashMap<String, Object>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    final DataResult<HashMap<String, Object>> result =
        this.responseFactory.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), errors);
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public @NotNull ResponseEntity<Object> handleItemNotFoundException(
      final @NotNull ItemNotFoundException ex) {
    log.info(ex.getMessage());

    final Result result = this.responseFactory.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public @NotNull ResponseEntity<Object> handleBadCredentialsException(
      final @NotNull BadCredentialsException ex) {
    log.info(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public @NotNull ResponseEntity<Object> handleUnauthorizedException(
      final @NotNull UnauthorizedException ex) {
    log.info(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(DuplicateItemException.class)
  public @NotNull ResponseEntity<Object> handleUnauthorizedException(
      final @NotNull DuplicateItemException ex) {
    log.info(ex.getMessage());

    final Result result = this.responseFactory.error(HttpStatus.CONFLICT.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.CONFLICT);
  }
}
