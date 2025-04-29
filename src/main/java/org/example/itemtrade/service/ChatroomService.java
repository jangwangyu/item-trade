package org.example.itemtrade.service;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatRoomDto;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatroomService {

  private final ChatRoomRepository chatroomRepository;
  private final ItemPostRepository itemPostRepository;
  private final ChatMessageRepository chatMessageRepository;

  // 채팅방 생성
  public ChatRoom createChatRoom(CustomOAuth2User buyer, Long postId) {

    ItemPost post = itemPostRepository.findById(postId).orElseThrow(() ->
        new IllegalArgumentException("게시글을 찾을 수 없습니다.")); // 게시글이 존재하는지 확인

    Member seller = post.getSeller(); // 판매자 정보 가져오기

    return chatroomRepository.findByItemPostAndBuyerAndSeller(post, buyer.getMember(), seller)
        .orElseGet(() -> {
          System.out.println("✅ ChatRoom 생성 요청: buyer=" + buyer.getMember().getId() + ", seller=" + seller.getId() + ", post=" + post.getId());
          ChatRoom chatRoom = ChatRoom.of(buyer.getMember(), seller, post);
          System.out.println("💾 ChatRoom 저장 시도: " + chatRoom);
          return chatroomRepository.save(chatRoom);
        });
  }

  // 채팅방 목록
  public List<ChatRoomDto> getChatRoomsForMember(CustomOAuth2User member) {
    if (member == null) {
      throw new IllegalStateException("로그인한 사용자만 채팅 목록을 조회할 수 있습니다.");
    }
    List<ChatRoom> asBuyer = chatroomRepository.findAllByBuyer(member.getMember());
    List<ChatRoom> asSeller = chatroomRepository.findAllBySeller(member.getMember());

    // 구매자 채팅방과 판매자 채팅방을 합쳐서 하나의 리스트로 반환
    return Stream.concat(asBuyer.stream(), asSeller.stream())
        .distinct()
        .map(room -> {
          ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(room.getId()).orElse(null);
          String lastContent = (lastMessage != null) ? lastMessage.getContent() : "";
          Long unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, member.getMember());
          System.out.println("📌 room.id = " + room.getId());
          return ChatRoomDto.from(room, unreadCount, lastContent);
        })
        .toList();
  }

  // 단일 채팅방 조회
  public ChatRoomDto getChatRoomById(Long id, Member member) {
    return chatroomRepository.findById(id)
        .map(room -> {
          Long unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, member);
          return ChatRoomDto.from(room, unreadCount, null);
        })
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
  }

  // 채팅방 삭제
  public void deleteChatRoom(Long id, Member user) {
    // 채팅방이 존재하는지 확인
    ChatRoom chatRoom = chatroomRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

    if(!chatRoom.getBuyer().getId().equals(user.getMember().getId())) {
      throw new IllegalArgumentException("채팅방 삭제 권한이 없습니다.");
    }
    chatroomRepository.deleteById(chatRoom.getId());
  }
}
