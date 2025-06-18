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
  private final ChatroomService chatroomService;

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

    chatRoom.restoreFor(sender);

    chatRoomRepository.save(chatRoom);
    ChatMessage message = ChatMessage.of(sender, chatRoom, content, type);
    chatRoom.setLastMessageCreatedAt(message.getCreatedAt());
    chatMessageRepository.save(message);

    System.out.println("Message saved: " + message.getContent());

    return ChatMessageDto.from(message);
  }

  // 메세지를 시간 순으로 정렬
  public List<ChatMessageDto> getMessageByRoom(Long roomId, Member sender) {
    // 채팅방 존재하는지 확인
    ChatRoom chatRoom = chatroomService.getChatRoomById(roomId);

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

    return chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member);
  }


}
