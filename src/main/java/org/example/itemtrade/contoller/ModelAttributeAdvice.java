package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.NotificationDto;
import org.example.itemtrade.dto.User.CustomOAuth2User;
import org.example.itemtrade.dto.User.CustomUserDetails;
import org.example.itemtrade.service.ChatRoomService;
import org.example.itemtrade.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class ModelAttributeAdvice {
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;

    @ModelAttribute("unreadRoomCount")
    public Long getUnreadRoomCount(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return 0L; // 로그인하지 않은 경우
        }
        return chatRoomService.getUnreadRoomCount(user.getMember());
    }

    @ModelAttribute
    public void addNotificationCount(@AuthenticationPrincipal Object principal, Model model) {
        Member member = null;
        if (principal instanceof CustomOAuth2User oAuth2User) {
            member = oAuth2User.getMember();
        } else if (principal instanceof CustomUserDetails user) {
            member = user.getMember();
        }
        if(member != null) {
            List<NotificationDto> notificationList = notificationService.findRecentByTarget(member);
            Long unreadNotificationCount = notificationService.countUnreadNotifications(member);

            model.addAttribute("notificationList", notificationList);
            model.addAttribute("unreadNotificationCount", unreadNotificationCount);
        }
    }
}
