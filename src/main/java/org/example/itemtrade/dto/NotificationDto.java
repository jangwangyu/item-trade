package org.example.itemtrade.dto;

import java.time.format.DateTimeFormatter;
import org.example.itemtrade.domain.Notification;

public record NotificationDto(
    Long id,
    String message,
    String url,
    boolean isRead,
    String createdAt,
    Long targetId,
    String targetEmail
) {
  public static NotificationDto from(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getMessage(),
        notification.getUrl(),
        notification.isRead(),
        notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        notification.getTarget().getId(),
        notification.getTarget().getEmail()
    );
  }
}
