package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatRoomDto;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.service.ChatroomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ChatRoomController {

  private final ChatroomService chatroomService;

  // 채팅방 목록
  @GetMapping("/chat-list")
  public String getChatRooms(Model model, @AuthenticationPrincipal CustomOAuth2User user) {
    // 채팅방 목록 조회
    List<ChatRoomDto> chatRooms = chatroomService.getChatRoomsForMember(user);

    model.addAttribute("chatRooms", chatRooms);

    return "/chat-list";
  }

  // 채팅방 생성
  @PostMapping("/chat/{postId}")
  public String createChatRoom(@AuthenticationPrincipal CustomOAuth2User user, @PathVariable Long postId) {

    // 채팅방 생성
    ChatRoom room = chatroomService.createChatRoom(user, postId);
    System.out.println("🌐 요청 도착: itemPostId=" + postId);
    return "redirect:/chat/" + room.getId();
  }

  // 채팅방 조회
  @GetMapping("/chat/{chatRoomId}")
  public String getChatRoom(Model model, @PathVariable Long chatRoomId, @AuthenticationPrincipal CustomOAuth2User user) {
    // 상세조회
    ChatRoomDto room = chatroomService.getChatRoomById(chatRoomId, user.getMember());
    model.addAttribute("room", room);
    model.addAttribute("chatRoomId", chatRoomId);
    model.addAttribute("currentUserId", user.getMember().getId());

    return "/chat";
  }


  // 채팅방 삭제
  @PostMapping("/{postId}/delete")
  public String deleteChatRoom(@PathVariable Long postId, @AuthenticationPrincipal Member user) {
    // 채팅방 삭제
    chatroomService.deleteChatRoom(postId, user);

    return "redirect:/chatrooms";
  }
}
