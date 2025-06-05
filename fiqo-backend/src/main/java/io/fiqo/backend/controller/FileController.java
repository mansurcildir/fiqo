package io.fiqo.backend.controller;

import io.fiqo.backend.data.dto.file.FileInfo;
import io.fiqo.backend.data.dto.user.UserDetails;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {

  private static final String REQUEST_PARAM_PATH = "path";

  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull StorageService storageService;

  @PutMapping
  public ResponseEntity<Result> uploadFile(
      final @NotNull Authentication authentication,
      @RequestParam(REQUEST_PARAM_PATH) final String path,
      final @NotNull HttpServletRequest request)
      throws Exception {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.storageService.uploadFile(userDetails.userUuid(), path, request.getInputStream());
    return ResponseEntity.ok(
        this.responseFactory.success(HttpStatus.OK.value(), "uploadSuccessful"));
  }

  @GetMapping("/info")
  public ResponseEntity<Result> listFiles(
      final @NotNull Authentication authentication,
      @RequestParam(REQUEST_PARAM_PATH) final String path)
      throws Exception {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<FileInfo> files = this.storageService.listFiles(userDetails.userUuid(), path);
    return ResponseEntity.ok(
        this.responseFactory.success(HttpStatus.OK.value(), "fetchedSuccessfully", files));
  }

  @GetMapping
  public ResponseEntity<byte[]> downloadFile(
      final @NotNull Authentication authentication,
      @RequestParam(REQUEST_PARAM_PATH) final String path)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final byte[] fileBytes = this.storageService.downloadFile(userDetails.userUuid(), path);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + Path.of(path).getFileName() + "\"")
        .body(fileBytes);
  }

  @DeleteMapping
  public ResponseEntity<Result> removeFiles(
      final @NotNull Authentication authentication,
      @RequestParam(REQUEST_PARAM_PATH) final String path,
      @RequestParam("recursive") final boolean recursive)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.storageService.removeFile(userDetails.userUuid(), path, recursive);
    return ResponseEntity.ok(
        this.responseFactory.success(HttpStatus.OK.value(), "removeSuccessful"));
  }
}
