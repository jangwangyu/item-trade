package org.example.itemtrade.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatMessageDto;
import org.example.itemtrade.dto.request.ChatMessageRequest;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("비즈니스 로직 - 채팅 메세지")
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;
  @Mock
  private ChatMessageRepository chatMessageRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private Principal principal;
  @InjectMocks
  private ChatMessageService chatMessageService;
  @Mock
  private ChatroomService chatroomService;

  @Test
  void 메세지_전송_정상() {
    // given
    Long roomId = 1L;
    Member sender = new Member("test@test.com", "완구");
    sender.setId(1L);

    ChatRoom chatRoom = mock(ChatRoom.class);
    given(memberRepository.findById(1L)).willReturn(Optional.of(sender));
    given(chatroomService.getChatRoomById(roomId)).willReturn(chatRoom);

    ChatMessage message = ChatMessage.of(sender, chatRoom, "test", "TEXT");
    given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(message);

    ChatMessageRequest request = new ChatMessageRequest();
    request.setContent("test");
    request.setSenderId(1L);
    request.setType("TEXT");
    // when
     ChatMessageDto result = chatMessageService.sendMessage(roomId, request);

    // then
     assertThat(result.content()).isEqualTo("test");
      assertThat(result.senderId()).isEqualTo(1L);
      assertThat(result.type()).isEqualTo("TEXT");
      verify(memberRepository).findById(sender.getId());
      verify(chatroomService).getChatRoomById(roomId);
      verify(chatMessageRepository).save(any(ChatMessage.class));
  }
  @Test
  void 메세지_전송_사용자_존재하지않을때_예외발생() {
    // Given
    Long roomId = 1L;
    ChatMessageRequest request = new ChatMessageRequest();
    request.setContent("test");
    request.setSenderId(1L);
    request.setType("TEXT");

    given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatMessageService.sendMessage(roomId, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("사용자가 존재하지 않습니다.");
  }
  @Test
  void 메세지_전송_채팅방_존재하지않을때_예외발생() {
    // Given
    ChatMessageRequest request = new ChatMessageRequest();
    Member sender = new Member("test@test.com", "완구");
    sender.setId(1L);
    request.setContent("test");
    request.setSenderId(1L);
    request.setType("TEXT");

    given(memberRepository.findById(1L)).willReturn(Optional.of(sender));
    // When & Then
    assertThatThrownBy(() -> chatMessageService.sendMessage(2L, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("채팅방이 존재하지 않습니다.");
  }


  @Test
  void 읽음처리_정상() {
    // Given
    Member sender1 = new Member("test@test.com", "받는사람"); // 요청 사용자
    sender1.setId(1L);

    Member sender2 = new Member("test@test.com", "보낸사람");; // 메세지를 보낸 사용자
    sender2.setId(2L);

    ChatRoom chatRoom = mock(ChatRoom.class);
    chatRoom.setId(1L);
    ChatMessage message = new ChatMessage(sender2, chatRoom, "test", "TEXT");
    message.setRead(false);

    given(chatroomService.getChatRoomById(1L)).willReturn(chatRoom);
    given(chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom)).willReturn(List.of(message));
    // When
    List<ChatMessageDto> result = chatMessageService.getMessageByRoom(1L, sender1);
    // Then
    assertThat(result).hasSize(1);
    assertThat(message.isRead()).isTrue();
  }
  @Test
  void 읽음처리_채팅방_존재하지않을때_예외발생() {
    // Given
    Member sender1 = new Member("test@test.com", "받는사람"); // 요청 사용자
    sender1.setId(1L);

    Member sender2 = new Member("test@test.com", "보낸사람");; // 메세지를 보낸 사용자
    sender2.setId(2L);

    ChatRoom chatRoom = mock(ChatRoom.class);
    chatRoom.setId(1L);
    ChatMessage message = new ChatMessage(sender2, chatRoom, "test", "TEXT");
    message.setRead(false);

    // When & Then
    assertThatThrownBy(() -> chatMessageService.getMessageByRoom(2L, sender1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("채팅방이 존재하지 않습니다.");
  }

  @Test
  void 정상조회() {
    // Given
    ChatRoom chatRoom = mock(ChatRoom.class);
    Member member = new Member("test.com", "완구");

    given(chatroomService.getChatRoomById(1L)).willReturn(chatRoom);
    given(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member)).willReturn(5L);
    // When
    Long count = chatMessageService.UnreadMessageCount(1L, member);
    // Then
    assertThat(count).isEqualTo(5L);
  }

  @Test
  void 정상조회_채팅방_존재하지않을때_예외발생() {
    // Given
    Member member = new Member("test.com", "완구");

    // When & Then
    assertThatThrownBy(() -> chatMessageService.UnreadMessageCount(1L, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("채팅방이 존재하지 않습니다.");
  }
}