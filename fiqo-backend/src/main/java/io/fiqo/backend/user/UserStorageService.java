package io.fiqo.backend.user;

import io.fiqo.backend.storage.StorageStrategy;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserStorageService {

  private static final String AVATAR_SUFFIX = "avatar.png";

  @Value("${storage.avatar.path}")
  private String avatarPath;

  private final @NotNull StorageStrategy storageStrategy;

  public void uploadAvatar(final @NotNull InputStream inputStream, final @NotNull UUID userUuid)
      throws Exception {
    final byte[] fileBytes = inputStream.readAllBytes();

    final String path =
        this.avatarPath + File.separator + userUuid + File.separator + AVATAR_SUFFIX;
    this.storageStrategy.upload(path, fileBytes);
  }

  public byte[] getAvatar(final @NotNull UUID userUuid) {
    try {
      final String path =
          this.avatarPath + File.separator + userUuid + File.separator + AVATAR_SUFFIX;
      return this.storageStrategy.download(path);
    } catch (final Exception e) {
      return new byte[0];
    }
  }
}
