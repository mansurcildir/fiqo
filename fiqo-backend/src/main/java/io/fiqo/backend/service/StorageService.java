package io.fiqo.backend.service;

import io.fiqo.backend.data.dto.file.FileInfo;
import io.fiqo.backend.data.entity.File;
import io.fiqo.backend.data.entity.User;
import io.fiqo.backend.exception.DuplicateItemException;
import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.mapper.FileConverter;
import io.fiqo.backend.repository.FileRepository;
import io.fiqo.backend.repository.UserRepository;
import io.fiqo.backend.storage.StorageStrategy;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageService {

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
    this.storageStrategy.upload(path, inputStream.readAllBytes());

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Optional<File> opt =
        this.fileRepository.findByPathAndUserIdAndDeletedFalse(path, user.getId());

    if (opt.isPresent()) {
      throw new DuplicateItemException("duplicateItem");
    }

    final String name = Path.of(relativePath).getFileName().toString();
    final String extension = FilenameUtils.getExtension(relativePath);

    final File file =
        File.builder()
            .uuid(UUID.randomUUID())
            .name(name)
            .extension(extension)
            .path(path)
            .user(user)
            .build();

    this.fileRepository.save(file);
  }

  public byte[] downloadFile(final @NotNull UUID userUuid, final @NotNull String path)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final File file =
        this.fileRepository
            .findByPathAndUserIdAndDeletedFalse(path, user.getId())
            .orElseThrow(() -> new ItemNotFoundException("fileNotFound"));

    return this.storageStrategy.download(file.getPath());
  }

  public void removeFile(
      final @NotNull UUID userUuid, final @NotNull String relativePath, final boolean recursive)
      throws Exception {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final String path = userUuid + "/" + relativePath;
    this.storageStrategy.remove(path, recursive);

    if (recursive) {
      this.fileRepository.deleteAllByPathStartingWithAndUserIdAndDeletedFalse(path, user.getId());
      return;
    }
    this.fileRepository.deleteByPathAndUserIdAndDeletedFalse(path, user.getId());
  }

  public @NotNull List<FileInfo> listFiles(
      final @NotNull UUID userUuid, final @NotNull String relativePath) throws Exception {

    final String path = userUuid + "/" + relativePath;
    List<FileInfo> a = this.storageStrategy.list(path);
    return this.storageStrategy.list(path);
  }
}
