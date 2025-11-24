package io.fiqo.backend.file;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.exception.SubscriptionLimitExceed;
import io.fiqo.backend.file.dto.FileInfo;
import io.fiqo.backend.storage.StorageStrategy;
import io.fiqo.backend.user.User;
import io.fiqo.backend.user.UserRepository;
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
public class FileService {

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

    final MessageDigest digest = MessageDigest.getInstance(SHA_256);

    final byte[] fileBytes = inputStream.readAllBytes();
    final byte[] hashBytes = digest.digest(fileBytes);
    final long size = fileBytes.length;
    final String hashHex = Hex.encodeHexString(hashBytes);

    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    this.checkLimitExceeded(user, size);

    this.storageStrategy.upload(path, fileBytes);
    this.userRepository.updateDailyBandwidthByUuid(user.getUuid(), size);

    this.createOrUpdateFile(user, relativePath, hashHex, size);
  }

  public byte[] downloadFile(final @NotNull UUID userUuid, final @NotNull String relativePath)
      throws Exception {

    final String path = userUuid + "/" + relativePath;
    final byte[] bytes = this.storageStrategy.download(path);
    this.userRepository.updateDailyBandwidthByUuid(userUuid, bytes.length);

    return bytes;
  }

  public void removeFile(final @NotNull UUID userUuid, final @NotNull String relativePath)
      throws Exception {

    final String path = userUuid + "/" + relativePath;

    this.storageStrategy.removeFile(path);
    this.updateUsage(userUuid, relativePath);

    this.fileRepository.deleteByUserUuidAndPath(userUuid, relativePath, Instant.now());
  }

  public void removeFiles(final @NotNull UUID userUuid, final @NotNull String relativePath)
      throws Exception {

    final String path = userUuid + "/" + relativePath;

    this.storageStrategy.removeFiles(path);
    this.updateUsages(userUuid, path);

    this.fileRepository.deleteAllByUserUuidAndPathStartingWith(userUuid, path, Instant.now());
  }

  public @NotNull List<FileInfo> listFiles(
      final @NotNull UUID userUuid, final @NotNull String relativePath) {

    return this.fileRepository.findAllByUserUuidAndPathStartingWith(userUuid, relativePath).stream()
        .map(this.fileConverter::toFileInfo)
        .toList();
  }

  public void copyFile(
      final @NotNull UUID userUuid,
      final @NotNull String sourceRelativePath,
      final @NotNull String targetRelativePath)
      throws Exception {

    final String sourcePath = userUuid + "/" + sourceRelativePath;
    final String targetPath = userUuid + "/" + targetRelativePath;

    final File file =
        this.fileRepository
            .findByUserUuidAndPath(userUuid, sourceRelativePath)
            .orElseThrow(() -> new ItemNotFoundException("fileNotFound"));

    this.storageStrategy.copyFile(sourcePath, targetPath);

    this.createFile(file.getUser(), targetRelativePath, file.getDigest(), file.getSize());
    this.fileRepository.save(file);
  }

  public void pasteFile(
      final @NotNull UUID userUuid,
      final @NotNull String sourceRelativePath,
      final @NotNull String targetRelativePath)
      throws Exception {

    this.copyFile(userUuid, sourceRelativePath, targetRelativePath);
    this.removeFile(userUuid, sourceRelativePath);
  }

  private void checkLimitExceeded(final @NotNull User user, final long size) {
    if (user.getTotalFileSize() + size > user.getMaxFileSize()) {
      throw new SubscriptionLimitExceed("limitExceeded");
    }
  }

  private void createOrUpdateFile(
      final @NotNull User user,
      final @NotNull String relativePath,
      final @NotNull String hashHex,
      final long size) {

    final Optional<File> file =
        this.fileRepository.findByUserUuidAndPath(user.getUuid(), relativePath);

    if (file.isEmpty()) {
      this.createFile(user, relativePath, hashHex, size);
    } else {
      this.updateFile(user.getUuid(), file.get(), hashHex, size);
    }
  }

  private void createFile(
      final @NotNull User user,
      final @NotNull String relativePath,
      final @NotNull String hashHex,
      final long size) {
    final String name = Path.of(relativePath).getFileName().toString();
    final String extension = FilenameUtils.getExtension(relativePath);

    final File file =
        File.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .extension(extension)
            .path(relativePath)
            .digest(SHA_256_PREF + ":" + hashHex)
            .size(size)
            .user(user)
            .build();

    this.fileRepository.save(file);
    this.userRepository.updateTotalFileSizeByUuid(user.getUuid(), size);
    this.userRepository.updateDailyBandwidthByUuid(user.getUuid(), size);
  }

  private void updateFile(
      final @NotNull UUID userUuid,
      final @NotNull File file,
      final @NotNull String hashHex,
      final long size) {
    file.setDigest(SHA_256_PREF + ":" + hashHex);
    file.setSize(size);
    file.setUpdatedAt(Instant.now());

    this.fileRepository.save(file);
    this.userRepository.updateTotalFileSizeByUuid(userUuid, size - file.getSize());
    this.userRepository.updateDailyBandwidthByUuid(userUuid, size);
  }

  private void updateUsage(final @NotNull UUID userUuid, final @NotNull String relativePath) {
    final File file =
        this.fileRepository
            .findByUserUuidAndPath(userUuid, relativePath)
            .orElseThrow(() -> new ItemNotFoundException("fileNotFound"));

    this.userRepository.updateTotalFileSizeByUuid(userUuid, -1 * file.getSize());
    this.userRepository.updateDailyBandwidthByUuid(userUuid, file.getSize());
  }

  private void updateUsages(final @NotNull UUID userUuid, final @NotNull String relativePath) {
    final List<FileInfo> files = this.listFiles(userUuid, relativePath);
    for (FileInfo file : files) {
      this.userRepository.updateTotalFileSizeByUuid(userUuid, -1 * file.getSize());
      this.userRepository.updateDailyBandwidthByUuid(userUuid, file.getSize());
    }
  }
}
