package io.fiqo.backend.notification;

import io.fiqo.backend.notification.dto.NotificationInfo;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.UserDetails;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final @NotNull NotificationService notificationService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping
  public @NotNull ResponseEntity<Result> getNotifications(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<NotificationInfo> notifications =
        this.notificationService.getAllNotifications(userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(
                HttpStatus.OK.value(), "notificationsFetched", notifications));
  }

  @PostMapping("/{notificationUuid}/read")
  public @NotNull ResponseEntity<Result> readNotification(
      final @NotNull Authentication authentication, final @PathVariable String notificationUuid) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.notificationService.readNotification(
        userDetails.userUuid(), UUID.fromString(notificationUuid));

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "notificationRead"));
  }

  @DeleteMapping("/{notificationUuid}")
  public @NotNull ResponseEntity<Result> deleteNotification(
      final @NotNull Authentication authentication, final @PathVariable String notificationUuid) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.notificationService.deleteNotification(
        userDetails.userUuid(), UUID.fromString(notificationUuid));

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "notificationDeleted"));
  }

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public @NotNull SseEmitter notificationStream(final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return this.notificationService.createEmitter(userDetails.userUuid());
  }
}
