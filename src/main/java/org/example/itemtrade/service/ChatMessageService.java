package org.example.itemtrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatMessageDto;
import org.example.itemtrade.dto.request.ChatMessageRequest;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatMessageService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;
  private final ChatRoomService chatroomService;

  // 메세지 전송
  public ChatMessageDto sendMessage(Long roomId, ChatMessageRequest request) {
    //사용자 존재하는지 확인
    Long senderId = request.getSenderId();

    Member sender = memberRepository.findById(senderId).orElseThrow(() ->
        new IllegalArgumentException("사용자가 존재하지 않습니다."));
    return sendMessages(roomId, sender, request.getContent(), request.getType());
  }

  @Transactional
  public ChatMessageDto sendMessages(Long roomId, Member sender, String content, String type) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    if (chatRoom == null) {
      throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
    }

    chatRoom.restoreFor(sender);

    chatRoomRepository.save(chatRoom);
    ChatMessage message = ChatMessage.of(sender, chatRoom, content, type);
    chatRoom.setLastMessageCreatedAt(message.getCreatedAt());
    chatMessageRepository.save(message);


    return ChatMessageDto.from(message);
  }

  // 메세지를 시간 순으로 정렬
  public List<ChatMessageDto> getMessageByRoom(Long roomId, Member sender) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    if (chatRoom == null) {
      throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
    }
    List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom);

    // 아직 안읽은 메세지 중 내가 보낸게 아닌 메세지만 읽음 처리
    messages.stream()
        .filter(m -> !m.isRead() && !m.getSender().getId().equals(sender.getId()))
        .forEach(m -> {
          m.setRead(true);
        });

    return messages.stream().map(ChatMessageDto::from).toList();
  }


  // 읽지 않은 메세지 조회
  public Long UnreadMessageCount(Long roomId, Member member) {
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    if (chatRoom == null) {
      throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
    }
    return chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member);
  }

  // 거래 완료 메세지 전송
  public void sendTradeMessage(Long roomId, Member sender) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    chatroomService.completeTrade(roomId, sender);
    Member opponent = sender.equals(chatRoom.getBuyer()) ? chatRoom.getBuyer() : chatRoom.getSeller();
    String message = sender.getNickName() + "님이 거래를 완료했습니다.";
    chatRoom.restoreFor(sender);

    sendMessages(roomId, sender, message, "TEXT");
  }

  // 거래 완료 메세지 전송
  public void sendCancelTradeMessage(Long roomId, Member sender) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    chatroomService.completeTrade(roomId, sender);
    Member opponent = sender.equals(chatRoom.getBuyer()) ? chatRoom.getBuyer() : chatRoom.getSeller();
    String message = sender.getNickName() + "님이 거래를 취소했습니다.";
    chatRoom.restoreFor(sender);

    sendMessages(roomId, sender, message, "TEXT");
  }

  // 거래 완료 메세지 전송
  public void sendReTradeMessage(Long roomId, Member sender) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);
    chatroomService.completeTrade(roomId, sender);
    Member opponent = sender.equals(chatRoom.getBuyer()) ? chatRoom.getBuyer() : chatRoom.getSeller();
    String message = sender.getNickName() + "님이 거래를 재요청했습니다.";
    chatRoom.restoreFor(sender);

    sendMessages(roomId, sender, message, "TEXT");
  }
}
