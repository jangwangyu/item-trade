package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.loader.internal.AliasConstantsHelper.get;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatMessageDto;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.example.itemtrade.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatMessageControllerTest {
  @LocalServerPort
  private int port;

  private WebSocketStompClient stompClient;

  @Autowired
  private ChatMessageService chatMessageService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ChatRoomRepository chatRoomRepository;
  @Autowired
  private ChatMessageRepository chatMessageRepository;

  @BeforeEach
  void setUp() {
    stompClient = new WebSocketStompClient(new SockJsClient(
        List.of(new WebSocketTransport(new StandardWebSocketClient()))
    ));
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
  }
  @Test
  void 메세지전송및수신() throws Exception{
    String url = "ws://localhost:" + port + "/ws"; // WebSocket URL 설정

    CompletableFuture<ChatMessageDto> future = new CompletableFuture<>();

    StompSession session = stompClient
        .connectAsync(url, new StompSessionHandlerAdapter() {})
        .get(1, TimeUnit.SECONDS);
    // 구독
    session.subscribe("/topic/chat/1", new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return ChatMessageDto.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        future.complete((ChatMessageDto) payload);
      }
    });

    // 메세지 전송
    ChatMessageDto message = new ChatMessageDto(1L, "test", "test", "test.png", false, null, 1L, "TEXT");
    session.send("/topic/chat/1", message);

    // 응답
    ChatMessageDto result = future.get(1, TimeUnit.SECONDS);
    assertThat(result.content()).isEqualTo("test");
  }

  @Test
  void 메세지조회및시간순정렬() throws Exception{
    // Given
    Member sender = memberRepository.save(Member.builder().email("test@test.com").nickName("sender").build());
    Member reciver = memberRepository.save(Member.builder().email("test2@test.com").nickName("reciver").build());
    ChatRoom room = chatRoomRepository.save(ChatRoom.builder()
        .buyer(reciver)
        .seller(sender)
        .build());
    ChatMessage msg1 = chatMessageRepository.save(ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content("첫 번째 메시지")
        .isRead(false)
        .type("TEXT")
        .createdAt(LocalDateTime.of(2025, 12, 3, 5, 30))
        .build());

    ChatMessage msg2 = chatMessageRepository.save(ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content("두 번째 메시지")
        .isRead(false)
        .type("TEXT")
        .createdAt(LocalDateTime.of(2025, 12, 3, 5, 35))
        .build());
    // When
    List<ChatMessageDto> result = chatMessageService.getMessageByRoom(room.getId(), reciver);
    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).content()).isEqualTo("첫 번째 메시지");
    assertThat(result.get(1).content()).isEqualTo("두 번째 메시지");

    ChatMessage reloadedMsg1 = chatMessageRepository.findById(msg1.getId()).orElseThrow();
    ChatMessage reloadedMsg2 = chatMessageRepository.findById(msg2.getId()).orElseThrow();
    assertThat(reloadedMsg1.isRead()).isTrue(); // 읽음 처리 확인
    assertThat(reloadedMsg2.isRead()).isTrue(); // 읽음 처리 확인
  }
}