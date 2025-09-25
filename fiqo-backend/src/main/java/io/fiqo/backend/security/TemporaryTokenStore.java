package io.fiqo.backend.security;

import io.fiqo.backend.user.dto.AuthResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TemporaryTokenStore {

  private final @NotNull Map<String, AuthResponse> store = new ConcurrentHashMap<>();

  public void put(final @NotNull String sessionId, final @NotNull AuthResponse response) {
    this.store.put(sessionId, response);
  }

  public @NotNull AuthResponse get(final @NotNull String sessionId) {
    return this.store.remove(sessionId);
  }
}
