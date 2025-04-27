package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.ChatRoomDto;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("비즈니스 로직 - 채팅방")
@ExtendWith(MockitoExtension.class)
class ChatroomServiceTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ItemPostRepository itemPostRepository;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @InjectMocks
  private ChatroomService chatroomService;

  @Test
  void 채팅방생성_기존에없을경우() {
    // given
    Long postId = 1L;
    ItemPost post = mock(ItemPost.class);
    Member buyerMember = new Member("test@test.com", "완구");
    CustomOAuth2User buyer = mock(CustomOAuth2User.class);
    ChatRoom chatRoom = mock(ChatRoom.class);
    Member seller = mock(Member.class);

    when(buyer.getMember()).thenReturn(buyerMember);
    when(post.getSeller()).thenReturn(seller); // 판매자 정보 가져오기
    when(itemPostRepository.findById(postId)).thenReturn(Optional.of(post)); // 게시글을 찾으면 post 반환
    when(chatRoomRepository.findByItemPostAndBuyerAndSeller(post,buyerMember,seller))
        .thenReturn(Optional.empty()); // 기존 채팅방이 없다고 가정함
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom); // 새로 만든 채팅방을 반환

    // when
    ChatRoom result = chatroomService.createChatRoom(buyer, postId);

    // then
    ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);
    verify(chatRoomRepository).save(captor.capture());

    ChatRoom savedChatRoom = captor.getValue();
    assertEquals(chatRoom, result); // 반환된 채팅방이 새로 만든 chatRoom과 같은지 확인
    assertEquals(post, savedChatRoom.getItemPost());
    assertEquals(buyerMember, savedChatRoom.getBuyer());
    assertEquals(seller, savedChatRoom.getSeller());
  }

  @Test
  void 채팅방생성_기존에있을경우() {
    // given
    Long postId = 1L;
    ItemPost post = mock(ItemPost.class);
    Member buyerMember = new Member("test@test.com", "완구");
    CustomOAuth2User buyer = mock(CustomOAuth2User.class);
    ChatRoom existingRoom = mock(ChatRoom.class);
    Member seller = mock(Member.class);

    when(buyer.getMember()).thenReturn(buyerMember);
    when(post.getSeller()).thenReturn(seller); // 판매자 정보 가져오기
    when(itemPostRepository.findById(postId)).thenReturn(Optional.of(post)); // 게시글을 찾으면 post 반환
    when(chatRoomRepository.findByItemPostAndBuyerAndSeller(post,buyerMember,seller))
        .thenReturn(Optional.of(existingRoom)); // 기존 채팅방이 있다고 가정함

    // when
    ChatRoom result = chatroomService.createChatRoom(buyer, postId);

    // then
    verify(chatRoomRepository, never()).save(any());

    assertEquals(existingRoom, result); // 반환된 채팅방이  존재하는 chatRoom과 같은지 확인
  }

  @Test
  void 채팅방_목록() {
    // given
    ItemPost post = mock(ItemPost.class);
    Member member = new Member("test@test.com", "완구");
    ChatRoom chatRoom1 = ChatRoom.of(
        member,
        member,
        post
    );

    ChatRoom chatRoom2 = ChatRoom.of(
        member,
        member,
        post
    );

    when(chatRoomRepository.findAllByBuyer(member)).thenReturn(List.of(chatRoom1));
    when(chatRoomRepository.findAllBySeller(member)).thenReturn(List.of(chatRoom2));

    when(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom1, member))
        .thenReturn(2L);
    when(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom2, member))
        .thenReturn(5L);

    // when
    List<ChatRoomDto> result = chatroomService.getChatRoomsForMember(member);
    // then
    assertThat(result).hasSize(2);
    assertThat(result).extracting("unreadCount").containsExactly(2L, 5L);

    verify(chatRoomRepository).findAllBySeller(member);
    verify(chatRoomRepository).findAllByBuyer(member);

  }

  @Test
  void 채팅방_중복제거확인() {
    // Given
    Member member = new Member("test@test.com", "완구");
    ItemPost post = mock(ItemPost.class);

    ChatRoom chatRoom = ChatRoom.of(
        member,
        mock(Member.class),
        post
    );

    // 구매자 채팅방과 판매자 채팅방이 동일한 경우
    when(chatRoomRepository.findAllByBuyer(member)).thenReturn(List.of(chatRoom));
    when(chatRoomRepository.findAllBySeller(member)).thenReturn(List.of(chatRoom));

    when(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member))
        .thenReturn(0L);
    // When
    List<ChatRoomDto> result = chatroomService.getChatRoomsForMember(member);
    // Then
    assertThat(result).hasSize(1);

    verify(chatRoomRepository).findAllBySeller(member);
    verify(chatRoomRepository).findAllByBuyer(member);
  }

  @Test
  void 채팅방_조회_성공() {
    // Given
    Long chatRoomId = 1L;
    Member member = new Member("test@test", "완구");
    ItemPost post = ItemPost.builder()
        .title("Test Post")
        .price(1000)
        .description("desc")
        .category(Category.RPG)
        .seller(member)
        .build();
    ChatRoom chatRoom = ChatRoom.of(
        member,
        member,
        post
    );
    given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.of(chatRoom));
    given(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member)).willReturn(2L);
    // When
    ChatRoomDto result = chatroomService.getChatRoomById(chatRoomId, member);
    // Then
    assertThat(result.unreadCount()).isEqualTo(2);
    then(chatRoomRepository).should().findById(chatRoomId);
    then(chatMessageRepository).should().countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, member);
  }

  @Test
  void 채팅방_조회_실패_채팅방없음() {
    // Given
    Long chatRoomId = 1L;
    Member member = new Member("test@test", "완구");

    given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.empty());
    // When & Then
    assertThatThrownBy(() -> chatroomService.getChatRoomById(chatRoomId, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("채팅방을 찾을 수 없습니다.");

    then(chatRoomRepository).should().findById(chatRoomId);
    // 실패 테스트에선 repository 호출이 없어야함.
    then(chatMessageRepository).shouldHaveNoInteractions();
  }

  @Test
  void 채팅방_삭제_성공() {
    // Given
    Long chatRoomId = 1L;
    Member member = new Member("test@test", "완구");
    ChatRoom chatRoom = mock(ChatRoom.class);

    ReflectionTestUtils.setField(member, "id", 1L); // memberId를 1L로 세팅

    when(chatRoom.getId()).thenReturn(chatRoomId);// chatRoom.getId()를 호출하면 미리 설정한 chatRoomId(1L)를 반환
    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
    when(chatRoom.getBuyer()).thenReturn(member.getMember());

    // When
    chatroomService.deleteChatRoom(chatRoomId, member);

    // Then
    verify(chatRoomRepository).deleteById(chatRoomId);
  }

  @Test
  void 채팅방_삭제_실패_채팅방없음() {
    // Given
    Long chatRoomId = 1L;
    Member member = new Member("test@test", "완구");

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> chatroomService.deleteChatRoom(chatRoomId, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("채팅방을 찾을 수 없습니다.");

    verify(chatRoomRepository).findById(chatRoomId);
    verify(chatRoomRepository, never()).deleteById(any());
  }

  @Test
  void 채팅방_삭제_권한없음() {
    // Given
    Long chatRoomId = 1L;
    Member member = new Member("test@test", "완구");
    ChatRoom chatRoom = mock(ChatRoom.class);
    Member otherMember = new Member("other@test", "다른사람");

    ReflectionTestUtils.setField(member, "id", 1L); // memberId를 1L로 세팅
    ReflectionTestUtils.setField(otherMember, "id", 2L);

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
    when(chatRoom.getBuyer()).thenReturn(otherMember); // 다른 사용자가 구매자로 설정됨

    // When & Then
    assertThatThrownBy(() -> chatroomService.deleteChatRoom(chatRoomId, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("채팅방 삭제 권한이 없습니다.");

    verify(chatRoomRepository).findById(chatRoomId);
    verify(chatRoomRepository, never()).deleteById(any());
  }
}