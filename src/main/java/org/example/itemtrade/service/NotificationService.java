package org.example.itemtrade.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.Notification;
import org.example.itemtrade.dto.NotificationDto;
import org.example.itemtrade.repository.MemberRepository;
import org.example.itemtrade.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MemberRepository memberRepository;

  public void sendNotification(Member target, String message, String url) {
    Member fullTarget = memberRepository.findById(target.getId()).orElseThrow();
    // 알림 생성
    Notification notification = Notification.builder()
        .target(fullTarget)
        .message(message)
        .url(url)
        .build();

    // 알림 저장
    notificationRepository.save(notification);
    // WebSocket을 통해 알림 전송
    messagingTemplate.convertAndSendToUser(fullTarget.getEmail(), "/queue/notifications", NotificationDto.from(notification));
  }

  public List<NotificationDto> findAllByTarget(Member member) {
    // 현재 로그인 유저의 알림만 조회
    return notificationRepository.findAllByTarget(member).stream()
        .map(NotificationDto::from)
        .filter(notification -> !notification.isRead())
        .toList();
  }

  public void markAsRead(Long notificationId, Member member) throws AccessDeniedException {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
    if(!notification.getTarget().getId().equals(member.getId())) {
      throw new AccessDeniedException("잘못된 접근입니다.");
    }
    notification.setRead(true);
    notificationRepository.save(notification);
  }

}
