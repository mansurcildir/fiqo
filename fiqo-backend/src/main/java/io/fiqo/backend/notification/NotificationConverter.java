package io.fiqo.backend.notification;

import io.fiqo.backend.notification.dto.NotificationInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationConverter {
  NotificationInfo toNotificationInfo(Notification notification);
}
