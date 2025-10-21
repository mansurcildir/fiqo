package io.fiqo.backend.auth;

import static io.fiqo.backend.util.Constant.REFRESH_TOKEN;

import io.fiqo.backend.auth.dto.RecoverPasswordForm;
import io.fiqo.backend.auth.dto.ResetPasswordForm;
import io.fiqo.backend.mail.EmailForm;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.AuthResponse;
import io.fiqo.backend.user.dto.UserDetails;
import io.fiqo.backend.user.dto.UserLogin;
import io.fiqo.backend.user.dto.UserRegister;
import io.fiqo.backend.util.JwtUtil;
import io.fiqo.backend.verification.VerificationService;
import io.fiqo.backend.verification.VerificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final @NotNull AuthService authService;
  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull VerificationService verificationService;

  @PostMapping("/login")
  public @NotNull ResponseEntity<Result> login(
      @Valid @RequestBody final @NotNull UserLogin loginRequest) {
    final AuthResponse authResponse = this.authService.login(loginRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "loggedIn", authResponse));
  }

  @PostMapping("/register")
  public @NotNull ResponseEntity<Result> register(
      @Valid @RequestBody final @NotNull UserRegister userRegister) {
    final AuthResponse authResponse = this.authService.register(userRegister);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.responseFactory.success(HttpStatus.CREATED.value(), "registered", authResponse));
  }

  @PostMapping("/password-recovery")
  public @NotNull ResponseEntity<Result> sendPasswordRecovery(
      @Valid @RequestBody final @NotNull EmailForm emailForm) {
    this.verificationService.createVerification(emailForm, VerificationType.PASSWORD_RESET);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "passwordVerificationSent"));
  }

  @PutMapping("/recover-password")
  public @NotNull ResponseEntity<Result> recoverPassword(
      @Valid @RequestBody final @NotNull RecoverPasswordForm recoverPasswordForm) {
    this.authService.recoverPassword(recoverPasswordForm);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "passwordReset"));
  }

  @PutMapping("/reset-password")
  public @NotNull ResponseEntity<Result> resetPassword(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull ResetPasswordForm resetPasswordForm) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.authService.resetPassword(resetPasswordForm, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "passwordReset"));
  }

  @GetMapping("/logout")
  public @NotNull ResponseEntity<Result> logout(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.authService.logout(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "loggedOut"));
  }

  @GetMapping("/refresh")
  public @NotNull ResponseEntity<Result> refresh(
      @RequestHeader(REFRESH_TOKEN) final @NotNull String header) {
    final AuthResponse authResponse = this.authService.refresh(header);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "refreshed", authResponse));
  }

  @GetMapping("/token")
  public @NotNull ResponseEntity<Result> getSessionToken(
      @RequestParam final @NotNull String token) {
    final AuthResponse authResponse = this.jwtUtil.getAuthResponseFromToken(token);
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "tokensFetched", authResponse));
  }
}
