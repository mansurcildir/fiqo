package io.fiqo.backend.user;

import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.ProfileForm;
import io.fiqo.backend.user.dto.UserDetails;
import io.fiqo.backend.user.dto.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final @NotNull UserService userService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping("/profile")
  public @NotNull ResponseEntity<Result> getProfile(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final UserInfo userInfo = this.userService.getUserInfo(userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "profileFetched", userInfo));
  }

  @PutMapping("/profile")
  public @NotNull ResponseEntity<Result> updatePersonalInfo(
      final @NotNull Authentication authentication,
      @Valid @RequestBody final @NotNull ProfileForm profileForm) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.userService.updateProfile(profileForm, userDetails.userUuid());
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "profileUpdated"));
  }
}
