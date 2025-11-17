package io.fiqo.backend.notification;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.notification.dto.NotificationInfo;
import io.fiqo.backend.notification.dto.NotificationType;
import io.fiqo.backend.user.User;
import io.fiqo.backend.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

  private final @NotNull NotificationRepository notificationRepository;
  private final @NotNull UserRepository userRepository;
  private final @NotNull NotificationConverter notificationConverter;
  private final @NotNull Map<UUID, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

  public void createNotification(
      final @NotNull UUID userUuid,
      final @NotNull String title,
      final @NotNull String message,
      final @NotNull NotificationType type) {

    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Notification notification = new Notification();
    notification.setUuid(UUID.randomUUID());
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setType(type);
    notification.setUser(user);

    this.notificationRepository.save(notification);
    this.pushNotification(userUuid, this.notificationConverter.toNotificationInfo(notification));
  }

  public @NotNull List<NotificationInfo> getAllNotifications(final @NotNull UUID userUuid) {
    final List<Notification> notifications =
        this.notificationRepository.findAllByUserUuidOrderByCreatedAtDesc(userUuid);
    return notifications.stream().map(this.notificationConverter::toNotificationInfo).toList();
  }

  public void readNotification(final @NotNull UUID userUuid, final @NotNull UUID notificationUuid) {
    final Notification notification =
        this.notificationRepository
            .findByUserUuidAndUuid(userUuid, notificationUuid)
            .orElseThrow(() -> new ItemNotFoundException("notificationNotFound"));

    notification.setRead(true);
    this.notificationRepository.save(notification);
  }

  public void deleteNotification(
      final @NotNull UUID userUuid, final @NotNull UUID notificationUuid) {
    this.notificationRepository.deleteByUserUuidAndUuid(userUuid, notificationUuid, Instant.now());
  }

  public @NotNull SseEmitter createEmitter(final @NotNull UUID userUuid) {
    final SseEmitter emitter = new SseEmitter(0L);

    this.userEmitters.computeIfAbsent(userUuid, k -> new CopyOnWriteArrayList<>()).add(emitter);

    this.removeEmitter(userUuid, emitter);
    return emitter;
  }

  private void removeEmitter(final @NotNull UUID userUuid, final @NotNull SseEmitter emitter) {
    emitter.onCompletion(() -> this.userEmitters.get(userUuid).remove(emitter));
    emitter.onTimeout(() -> this.userEmitters.get(userUuid).remove(emitter));
  }

  private void pushNotification(
      final @NotNull UUID userUuid, final @NotNull NotificationInfo notificationInfo) {
    final List<SseEmitter> emitters = this.userEmitters.get(userUuid);
    if (emitters != null) {
      this.sendEmitters(emitters, notificationInfo);
    }
  }

  private void sendEmitters(
      final @NotNull List<SseEmitter> emitters, final @NotNull NotificationInfo notificationInfo) {
    for (final @NotNull SseEmitter emitter : emitters) {
      this.sendEmitter(emitters, emitter, notificationInfo);
    }
  }

  private void sendEmitter(
      final @NotNull List<SseEmitter> emitters,
      final @NotNull SseEmitter emitter,
      final @NotNull NotificationInfo notificationInfo) {
    try {
      emitter.send(SseEmitter.event().name("notification").data(notificationInfo));
    } catch (Exception e) {
      emitter.complete();
      emitters.remove(emitter);
    }
  }
}
