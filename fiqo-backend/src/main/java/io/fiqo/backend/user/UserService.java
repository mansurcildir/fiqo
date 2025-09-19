package io.fiqo.backend.user;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.user.dto.UserInfo;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
  private final @NotNull UserRepository userRepository;
  private final @NotNull UserConverter userConverter;

  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }
}
