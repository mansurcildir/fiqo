package io.fiqo.backend.controller;

import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.storage.StorageStrategy;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/file")
@RequiredArgsConstructor
public class FileController {

  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull StorageStrategy storageStrategy;

  @PostMapping("/upload")
  public ResponseEntity<Result> uploadFile(
      @RequestParam("path") String path, @RequestParam("file") MultipartFile file)
      throws Exception {

    this.storageStrategy.uploadFile(path, file);
    return ResponseEntity.ok(
        this.responseFactory.success(HttpStatus.OK.value(), "uploadSuccessful"));
  }
}
