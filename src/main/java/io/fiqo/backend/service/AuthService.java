package io.fiqo.backend.service;

import io.fiqo.backend.data.dto.user.AuthResponse;
import io.fiqo.backend.data.dto.user.UserInfo;
import io.fiqo.backend.data.dto.user.UserLogin;
import io.fiqo.backend.data.dto.user.UserRegister;
import io.fiqo.backend.data.entity.RefreshToken;
import io.fiqo.backend.data.entity.Role;
import io.fiqo.backend.data.entity.User;
import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.exception.UnauthorizedException;
import io.fiqo.backend.mapper.UserConverter;
import io.fiqo.backend.repository.RefreshTokenRepository;
import io.fiqo.backend.repository.RoleRepository;
import io.fiqo.backend.repository.UserRepository;
import io.fiqo.backend.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull PasswordEncoder passwordEncoder;
  private final @NotNull UserRepository userRepository;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull RefreshTokenRepository refreshTokenRepository;
  private final @NotNull UserConverter userConverter;

  public @NotNull AuthResponse login(final @NotNull UserLogin loginRequest) {
    final User user =
        this.userRepository
            .findByUsernameAndDeletedFalse(loginRequest.username())
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final boolean matched =
        this.passwordEncoder.matches(loginRequest.password(), user.getPassword());

    if (user.getPassword() == null) {
      throw new UnauthorizedException("accessDenied");
    }

    if (!matched) {
      throw new UnauthorizedException("wrongPassword");
    }

    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();
    final String accessToken =
        this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    this.logout(user.getUuid());
    this.createRefreshToken(user, refreshToken);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public @NotNull AuthResponse register(final @NotNull UserRegister userRegister) {
    final String encodedPassword = this.passwordEncoder.encode(userRegister.password());
    final User user =
        this.userConverter.toUser(
            UserRegister.builder()
                .username(userRegister.username())
                .password(encodedPassword)
                .email(userRegister.email())
                .picture(userRegister.picture())
                .build());
    user.setUuid(UUID.randomUUID());

    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));
    user.setRoles(Set.of(role));

    this.userRepository.save(user);

    log.warn("User: {} registered to auth-service!", user.getUuid());

    final String accessToken =
        this.jwtUtil.generateAccessToken(
            user.getUuid(), user.getUsername(), List.of(role.getName()));

    final String refreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

    this.createRefreshToken(user, refreshToken);
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  public @NotNull AuthResponse refresh(final @NotNull String token) {
    final UUID userUuid = this.jwtUtil.getUserUuidFromRefreshToken(token);
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<String> roles = user.getRoles().stream().map(Role::getName).toList();

    final Optional<RefreshToken> refreshToken = this.refreshTokenRepository.findByToken(token);

    if (refreshToken.isPresent()) {
      final String accessToken =
          this.jwtUtil.generateAccessToken(user.getUuid(), user.getUsername(), roles);
      final String newRefreshToken = this.jwtUtil.generateRefreshToken(user.getUuid());

      return AuthResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
    }

    throw new UnauthorizedException("accessDenied");
  }

  public void logout(final @NotNull UUID userUuid) {
    this.refreshTokenRepository.deleteAllByUserUuid(userUuid);
  }

  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }

  private void createRefreshToken(final @NotNull User user, final @NotNull String token) {
    final RefreshToken refreshToken =
        RefreshToken.builder().uuid(UUID.randomUUID()).token(token).user(user).build();

    this.refreshTokenRepository.save(refreshToken);
  }
}
