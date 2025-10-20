package io.fiqo.backend.file;

import io.fiqo.backend.file.dto.FileInfo;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.UserDetails;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {

  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull FileService fileService;

  @PutMapping
  public @NotNull ResponseEntity<Result> uploadFile(
      final @NotNull Authentication authentication,
      @RequestParam final String path,
      final @NotNull HttpServletRequest request)
      throws Exception {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.fileService.uploadFile(userDetails.userUuid(), path, request.getInputStream());
    return ResponseEntity.ok(this.responseFactory.success(HttpStatus.OK.value(), "fileUploaded"));
  }

  @PutMapping(
      path = "multipart",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public @NotNull ResponseEntity<Result> uploadFile(
      final @NotNull Authentication authentication,
      @RequestParam final String path,
      @RequestParam("file") final @NotNull MultipartFile file)
      throws Exception {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.fileService.uploadFile(userDetails.userUuid(), path, file.getInputStream());
    return ResponseEntity.ok(this.responseFactory.success(HttpStatus.OK.value(), "fileUploaded"));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> listFiles(
      final @NotNull Authentication authentication, @RequestParam final String path) {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<FileInfo> files = this.fileService.listFiles(userDetails.userUuid(), path);
    return ResponseEntity.ok(
        this.responseFactory.success(HttpStatus.OK.value(), "fileFetched", files));
  }

  @GetMapping("/download")
  public @NotNull ResponseEntity<byte[]> downloadFile(
      final @NotNull Authentication authentication, @RequestParam final String path)
      throws Exception {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final byte[] fileBytes = this.fileService.downloadFile(userDetails.userUuid(), path);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + Path.of(path).getFileName() + "\"")
        .body(fileBytes);
  }

  @DeleteMapping
  public @NotNull ResponseEntity<Result> removeFile(
      final @NotNull Authentication authentication,
      @RequestParam final String path,
      @RequestParam final boolean recursive)
      throws Exception {

    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    if (recursive) {
      this.fileService.removeFiles(userDetails.userUuid(), path);
      return ResponseEntity.ok(this.responseFactory.success(HttpStatus.OK.value(), "filesRemoved"));
    } else {
      this.fileService.removeFile(userDetails.userUuid(), path);
      return ResponseEntity.ok(this.responseFactory.success(HttpStatus.OK.value(), "fileRemoved"));
    }
  }
}
