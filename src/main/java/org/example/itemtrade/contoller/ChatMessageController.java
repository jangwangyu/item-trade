package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatMessageDto;
import org.example.itemtrade.dto.request.ChatMessageRequest;
import org.example.itemtrade.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

  private final ChatMessageService chatMessageService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  // 전송
  @MessageMapping("/chat/{roomId}/send")
  public void sendMessage(@DestinationVariable Long roomId, @Payload ChatMessageRequest request) {
    ChatMessageDto message = chatMessageService.sendMessage(roomId, request);

    simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
  }

  // // 채팅방에 속한 메세지 조회, 시간순 정렬
  @GetMapping("/chat/{roomId}/messages")
  @ResponseBody
  public List<ChatMessageDto> getMessageByRoom(@PathVariable Long roomId, @AuthenticationPrincipal(expression = "member") Member member) {
    return chatMessageService.getMessageByRoom(roomId, member);
  }

  // 읽지 않은 메세지 조회
  @GetMapping("/chat/{roomId}/unread")
  @ResponseBody
  public Long unreadMessageCount(@PathVariable Long roomId, @AuthenticationPrincipal(expression = "member") Member member) {
    return chatMessageService.UnreadMessageCount(roomId, member);
  }
}
