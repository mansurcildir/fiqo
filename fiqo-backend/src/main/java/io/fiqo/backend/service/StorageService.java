package io.fiqo.backend.service;

import io.fiqo.backend.data.dto.file.FileInfo;
import io.fiqo.backend.data.entity.File;
import io.fiqo.backend.data.entity.User;
import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.mapper.FileConverter;
import io.fiqo.backend.repository.FileRepository;
import io.fiqo.backend.repository.UserRepository;
import io.fiqo.backend.storage.StorageStrategy;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageService {

  private static final String SHA_256 = "SHA-256";
  private static final String SHA_256_PREF = "sha256";

  private final @NotNull StorageStrategy storageStrategy;
  private final @NotNull FileRepository fileRepository;
  private final @NotNull UserRepository userRepository;
  private final @NotNull FileConverter fileConverter;

  public void uploadFile(
      final @NotNull UUID userUuid,
      final @NotNull String relativePath,
      final @NotNull InputStream inputStream)
      throws Exception {

    final String path = userUuid + "/" + relativePath;

    byte[] fileBytes = inputStream.readAllBytes();
    final MessageDigest digest = MessageDigest.getInstance(SHA_256);
    byte[] hashBytes = digest.digest(fileBytes);
    final String hashHex = Hex.encodeHexString(hashBytes);

    this.storageStrategy.upload(path, fileBytes);

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Optional<File> opt =
        this.fileRepository.findByPathAndUserUuidAndDeletedFalse(path, user.getUuid());

    File file;

    if (opt.isEmpty()) {
      final String name = Path.of(relativePath).getFileName().toString();
      final String extension = FilenameUtils.getExtension(relativePath);

      file =
          File.builder()
              .uuid(UUID.randomUUID())
              .name(name)
              .extension(extension)
              .path(path)
              .digest(SHA_256_PREF + ":" + hashHex)
              .user(user)
              .build();

    } else {
      file = opt.get();
      file.setDigest(SHA_256_PREF + ":" + hashHex);
      file.setUpdatedAt(Instant.now());
    }

    this.fileRepository.save(file);
  }

  public byte[] downloadFile(final @NotNull UUID userUuid, final @NotNull String path)
      throws Exception {
    final File file =
        this.fileRepository
            .findByPathAndUserUuidAndDeletedFalse(path, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("fileNotFound"));

    return this.storageStrategy.download(file.getPath());
  }

  public void removeFile(
      final @NotNull UUID userUuid, final @NotNull String relativePath, final boolean recursive)
      throws Exception {

    final String path = userUuid + "/" + relativePath;
    this.storageStrategy.remove(path, recursive);

    if (recursive) {
      this.fileRepository.deleteAllByPathStartingWithAndUserUuidAndDeletedFalse(path, userUuid);
      return;
    }
    this.fileRepository.deleteByPathAndUserUuidAndDeletedFalse(path, userUuid);
  }

  public @NotNull List<FileInfo> listFiles(
      final @NotNull UUID userUuid, final @NotNull String relativePath) {

    final String path = userUuid + "/" + relativePath;
    return this.fileRepository.findAllByPathStartingWithAndDeletedFalse(path).stream()
        .map(this.fileConverter::toFileInfo)
        .toList();
  }
}
