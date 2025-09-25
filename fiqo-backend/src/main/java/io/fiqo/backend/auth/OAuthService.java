package io.fiqo.backend.auth;

import io.fiqo.backend.auth.dto.OAuthUserInfo;
import io.fiqo.backend.user.dto.AuthResponse;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OAuthService {
  @NotNull
  AuthResponse login(@NotNull OAuthUserInfo userInfo);

  void connect(@NotNull UUID userUuid, @NotNull OAuthUserInfo userInfo);

  @Nullable
  String getPrimaryEmail(@NotNull String token);
}
