package io.fiqo.backend.auth;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.exception.UnauthorizedException;
import io.fiqo.backend.refresh_token.RefreshToken;
import io.fiqo.backend.refresh_token.RefreshTokenRepository;
import io.fiqo.backend.user.User;
import io.fiqo.backend.user.UserRepository;
import io.fiqo.backend.user.UserService;
import io.fiqo.backend.user.dto.AuthResponse;
import io.fiqo.backend.user.dto.UserInfo;
import io.fiqo.backend.user.dto.UserLogin;
import io.fiqo.backend.user.dto.UserRegister;
import io.fiqo.backend.user.role.Role;
import io.fiqo.backend.user.role.RoleRepository;
import io.fiqo.backend.util.JwtUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  private static final @NotNull String SUBSCRIPTION_PLAN_FREE = "Free";

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull PasswordEncoder passwordEncoder;
  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull RefreshTokenRepository refreshTokenRepository;
  private final @NotNull UserService userService;

  public @NotNull AuthResponse login(final @NotNull UserLogin loginRequest) {
    final User user =
        this.userRepository
            .findByUsername(loginRequest.username())
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    this.checkPasswordNotNull(user.getPassword());
    this.checkPasswordMatches(loginRequest.password(), user.getPassword());

    this.logout(user.getUuid());
    return this.createAuthResponse(user);
  }

  public @NotNull AuthResponse createAuthResponse(final @NotNull User user) {
    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();

    final String accessToken =
        this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    final RefreshToken token =
        RefreshToken.builder().uuid(UUID.randomUUID()).token(refreshToken).user(user).build();

    this.refreshTokenRepository.save(token);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @SneakyThrows
  public @NotNull AuthResponse register(final @NotNull UserRegister userRegister) {
    this.checkUsernameExists(userRegister.username());
    this.checkEmailExists(userRegister.email());

    String encodedPassword = null;
    if (userRegister.password() != null) {
      encodedPassword = this.passwordEncoder.encode(userRegister.password());
    }

    final User user =
        this.createUser(userRegister.username(), userRegister.email(), encodedPassword);

    log.warn("{} has been registered!", user.getEmail());
    return this.createAuthResponse(user);
  }

  public @NotNull AuthResponse refresh(final @NotNull String token) {
    final UUID userUuid = this.jwtUtil.getUserUuidFromRefreshToken(token);
    final UserInfo userInfo = this.userService.getUserInfo(userUuid);
    final List<String> roles = userInfo.roles();

    final Optional<RefreshToken> refreshToken = this.refreshTokenRepository.findByToken(token);

    this.checkRefreshTokenEmpty(refreshToken.isEmpty());

    final String accessToken =
        this.jwtUtil.generateAccessToken(userInfo.uuid(), userInfo.username(), roles);
    final String newRefreshToken = this.jwtUtil.generateRefreshToken(userInfo.uuid());

    return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
  }

  @CacheEvict(value = "users", key = "#userUuid")
  public void logout(final @NotNull UUID userUuid) {
    this.refreshTokenRepository.deleteAllByUserUuid(userUuid);
    this.refreshTokenRepository.flush();
  }

  public @NotNull User createUser(
      final @NotNull String username,
      final @NotNull String email,
      final @Nullable String password) {
    final User user =
        User.builder()
            .uuid(UUID.randomUUID())
            .username(username)
            .email(email)
            .password(password)
            .build();

    this.setRoles(user);
    return this.userRepository.save(user);
  }

  private void setRoles(final @NotNull User user) {
    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));

    user.setRoles(new HashSet<>(Set.of(role)));
  }

  private void checkPasswordNotNull(final @Nullable String password) {
    if (password == null) {
      throw new UnauthorizedException("accessDenied");
    }
  }

  private void checkPasswordMatches(
      final @NotNull String requestedPassword, final @NotNull String userPassword) {
    final boolean passwordMatches = this.passwordEncoder.matches(requestedPassword, userPassword);

    if (!passwordMatches) {
      throw new UnauthorizedException("wrongPassword");
    }
  }

  private void checkRefreshTokenEmpty(final boolean empty) {
    if (empty) {
      throw new UnauthorizedException("accessDenied");
    }
  }

  private void checkUsernameExists(final @NotNull String username) {
    if (this.userRepository.existsByUsername(username)) {
      throw new UnauthorizedException("usernameExists");
    }
  }

  private void checkEmailExists(final @NotNull String email) {
    if (this.userRepository.existsByEmail(email)) {
      throw new UnauthorizedException("emailExists");
    }
  }
}
