package org.example.itemtrade.contoller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.NotificationDto;
import org.example.itemtrade.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {
  private final NotificationService notificationService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/api/notifications")
  public List<NotificationDto> getNotifications(@AuthenticationPrincipal(expression = "member") Member member) {
    // 현재 로그인 유저의 알림만 조회
    return notificationService.findAllByTarget(member);
  }

  @PatchMapping("/api/notifications/{notificationId}/read")
  public void markAsRead(@PathVariable Long notificationId,
      @AuthenticationPrincipal(expression = "member") Member member) throws AccessDeniedException {
    // 알림 읽음 처리
    notificationService.markAsRead(notificationId, member);
  }
}
