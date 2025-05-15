package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatMessageDto;
import org.example.itemtrade.dto.ChatRoomDto;
import org.example.itemtrade.service.ChatMessageService;
import org.example.itemtrade.service.ChatroomService;
import org.example.itemtrade.service.MemberService;
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
  private final ChatMessageService chatMessageService;
  private final MemberService memberService;

  // 채팅방 목록
  @GetMapping("/chat-list")
  public String getChatRooms(Model model, @AuthenticationPrincipal(expression = "member") Member member) {
    // 채팅방 목록 조회
    List<ChatRoomDto> chatRooms = chatroomService.getChatRoomsForMember(member);

    model.addAttribute("chatRooms", chatRooms);

    return "/chat-list";
  }

  // 채팅방 생성
  @PostMapping("/chat/{postId}")
  public String createChatRoom(@AuthenticationPrincipal(expression = "member") Member member, @PathVariable Long postId) {

    // 채팅방 생성
    ChatRoom room = chatroomService.createChatRoom(member, postId);
    System.out.println("🌐 요청 도착: itemPostId=" + postId);
    return "redirect:/chat/" + room.getId();
  }

  // 채팅방 조회
  @GetMapping("/chat/{chatRoomId}")
  public String getChatRoom(Model model, @PathVariable Long chatRoomId, @AuthenticationPrincipal(expression = "member") Member member) {
    // 상세조회
    ChatRoomDto room = chatroomService.getChatRoomById(chatRoomId, member);
    List<ChatMessageDto> messages = chatMessageService.getMessageByRoom(chatRoomId, member);
    model.addAttribute("messages", messages);
    model.addAttribute("room", room);
    model.addAttribute("chatRoomId", chatRoomId);
    model.addAttribute("currentUserId", member.getId());

    model.addAttribute("isSeller",room.sellerId().equals(member.getId()));
    model.addAttribute("isBuyer", room.buyerId().equals(member.getId()));

    return "/chat";
  }


  // 채팅방 삭제
  @PostMapping("/chat/{postId}/delete")
  public String deleteChatRoom(@PathVariable Long postId, @AuthenticationPrincipal(expression = "member") Member member) {
    // 채팅방 삭제
    chatroomService.deleteChatRoom(postId, member);

    return "redirect:/chat-list";
  }

  // 회원 차단
  @PostMapping("/block/{targetId}")
  public String blockMember(@PathVariable Long targetId, @AuthenticationPrincipal(expression = "member") Member member, @RequestParam(required = false) Long chatRoomId) {

    Member blocker = member.getMember();
    Member blocked = memberService.getMemberId(targetId);

    memberService.blockMember(blocker, blocked);
    return "redirect:/mypage";
  }

  // 거래 완료
  @PostMapping("/chat/{chatRoomId}/complete")
  public String completeTrade(@PathVariable Long chatRoomId, @AuthenticationPrincipal(expression = "member") Member member) {
    // 거래 완료 처리
    chatroomService.completeTrade(chatRoomId, member);

    return "redirect:/chat/" + chatRoomId;
  }

  // 거래 취소
  @PostMapping("/chat/{chatRoomId}/cancel")
  public String cancelTrade(@PathVariable Long chatRoomId, @AuthenticationPrincipal(expression = "member") Member member) {
    // 거래 취소 처리
    chatroomService.cancelTrade(chatRoomId, member);

    return "redirect:/chat/" + chatRoomId;
  }

  // 재거래
  @PostMapping("/chat/{chatRoomId}/reopen")
  public String reopenTrade(@PathVariable Long chatRoomId, @AuthenticationPrincipal(expression = "member") Member member) {
    // 재거래 처리
    chatroomService.reopenTrade(chatRoomId, member);

    return "redirect:/chat/" + chatRoomId;
  }
}
