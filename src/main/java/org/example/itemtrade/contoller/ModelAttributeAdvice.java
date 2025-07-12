package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.User.CustomOAuth2User;
import org.example.itemtrade.service.ChatRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class ModelAttributeAdvice {
    private final ChatRoomService chatRoomService;

    @ModelAttribute("unreadRoomCount")
    public Long getUnreadRoomCount(@AuthenticationPrincipal CustomOAuth2User user) {
        if (user == null) {
            return 0L; // 로그인하지 않은 경우
        }
        return chatRoomService.getUnreadRoomCount(user.getMember());
    }
}
