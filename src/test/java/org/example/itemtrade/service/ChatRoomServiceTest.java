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
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("비즈니스 로직 - 채팅방")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ItemPostRepository itemPostRepository;

  @Mock
  private MemberBlockRepository memberBlockRepository;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @InjectMocks
  private ChatRoomService chatroomService;

  @Mock
  private MemberService memberService;

  @Test
  void 채팅방생성_기존에없을경우() {
    // given
    Long postId = 1L;
    ItemPost post = mock(ItemPost.class);
    Member buyer = new Member("test@test.com", "완구");
    ChatRoom chatRoom = mock(ChatRoom.class);
    Member seller = mock(Member.class);

    when(post.getSeller()).thenReturn(seller); // 판매자 정보 가져오기
    when(itemPostRepository.findById(postId)).thenReturn(Optional.of(post)); // 게시글을 찾으면 post 반환
    when(chatRoomRepository.findByItemPostAndBuyerAndSeller(post,buyer,seller))
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
    assertEquals(buyer, savedChatRoom.getBuyer());
    assertEquals(seller, savedChatRoom.getSeller());
  }

  @Test
  void 채팅방생성_기존에있을경우() {
    // given
    Long postId = 1L;
    ItemPost post = mock(ItemPost.class);
    Member buyer = new Member("test@test.com", "완구");
    ChatRoom existingRoom = mock(ChatRoom.class);
    Member seller = mock(Member.class);

    when(post.getSeller()).thenReturn(seller); // 판매자 정보 가져오기
    when(itemPostRepository.findById(postId)).thenReturn(Optional.of(post)); // 게시글을 찾으면 post 반환
    when(chatRoomRepository.findByItemPostAndBuyerAndSeller(post,buyer,seller))
        .thenReturn(Optional.of(existingRoom)); // 기존 채팅방이 있다고 가정함

    // when
    ChatRoom result = chatroomService.createChatRoom(buyer, postId);

    // then
    verify(chatRoomRepository, never()).save(any());

    assertEquals(existingRoom, result); // 반환된 채팅방이  존재하는 chatRoom과 같은지 확인
  }

  @DisplayName("로그인하지 않으면 예외가 발생한다")
  @Test
  void 로그인하지_않으면_예외가_발생한다() {
    // Given
    Member member = null; // 로그인하지 않은 상태
    // When & Then
    assertThatThrownBy(() -> chatroomService.getChatRoomsForMember(member))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("로그인한 사용자만 채팅 목록을 조회할 수 있습니다.");
  }

  @DisplayName("내가 차단한 유저는 채팅방 목록에서 제외된다")
  @Test
  void 내가_차단한_유저는_채팅방_목록에서_제외된다() {
    // Given
    Member me = new Member("test@test.com", "나");
    me.setId(1L);
    Member blocked = new Member("test@test.com", "상대");
    blocked.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        me,
        blocked,
        post
    );
    when(chatRoomRepository.findAllByMemberOrderByLastMessage(me)).thenReturn(List.of(chatRoom));
    when(memberService.isMemberBlocked(me, blocked)).thenReturn(true);
    // When
    List<ChatRoomDto> result = chatroomService.getChatRoomsForMember(me);
    // Then
    assertThat(result).isEmpty();
  }

  @DisplayName("상대가 나를 차단한 채팅방은 제외된다")
  @Test
  void 상대가_나를_차단한_채팅방은_제외된다() {
    // Given
    Member me = new Member("test@test.com", "나");
    me.setId(1L);
    Member blocker = new Member("test@test.com", "상대");
    blocker.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        blocker,
        me,
        post
    );
    when(chatRoomRepository.findAllByMemberOrderByLastMessage(me)).thenReturn(List.of(chatRoom));
    when(memberService.isMemberBlocked(blocker, me)).thenReturn(true);
    when(memberService.isMemberBlocked(me, blocker)).thenReturn(false);
    // When
    List<ChatRoomDto> result = chatroomService.getChatRoomsForMember(me);
    // Then
    assertThat(result).isEmpty();
  }

  @DisplayName("정상적인 채팅방은 반환된다(차단한 유저가 없음)")
  @Test
  void 정상적인_채팅방은_반환된다() {
    // Given
    Member me = new Member("test@test.com", "나");
    me.setId(1L);
    Member opponent = new Member("test@test.com", "상대");
    opponent.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        me,
        opponent,
        post
    );
    when(chatRoomRepository.findAllByMemberOrderByLastMessage(me)).thenReturn(List.of(chatRoom));
    when(memberService.isMemberBlocked(me, opponent)).thenReturn(false);
    when(memberService.isMemberBlocked(opponent, me)).thenReturn(false);
    when(chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(chatRoom, me))
        .thenReturn(0L); // 읽지 않은 메시지가 없다고 가정
    when(chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId()))
        .thenReturn(Optional.empty()); // 마지막 메시지가 없다고 가정
    // When
    List<ChatRoomDto> result = chatroomService.getChatRoomsForMember(me);
    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).unreadCount()).isEqualTo(0L);
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
    chatRoom.setId(chatRoomId);
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
    Member member = new Member("test@test", "완구");
    member.setId(1L);
    ChatRoom chatRoom = mock(ChatRoom.class);
    Long chatRoomId = 1L;
    chatRoom.setId(chatRoomId);

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
    when(chatRoom.getBuyer()).thenReturn(member.getMember());

    // When
    chatroomService.deleteChatRoom(chatRoomId, member);

    // Then
    verify(chatRoom).deleted(member);
    verify(chatRoomRepository).save(chatRoom);
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
    member.setId(1L);
    Member otherMember = new Member("other@test", "다른사람");
    otherMember.setId(2L);
    Member anonymous = new Member("other@test", "모르는사람");
    ChatRoom chatRoom = ChatRoom.builder()
        .seller(member)
        .buyer(otherMember)
        .build();
    chatRoom.setId(chatRoomId);


    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

    // When & Then
    assertThatThrownBy(() -> chatroomService.deleteChatRoom(chatRoomId, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("채팅방 삭제 권한이 없습니다.");

    verify(chatRoomRepository).findById(chatRoomId);
    verify(chatRoomRepository, never()).save(any());
  }

  @Test
  void 거래완료_구매자() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    chatRoom.setTradeBuyerComplete(true);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.completeTrade(1L, buyer);
    // Then
    assertThat(chatRoom.isTradeBuyerComplete()).isTrue();
    assertThat(chatRoom.isTradeSellerComplete()).isFalse();
    assertThat(chatRoom.getTradeStatus()).isNotEqualTo(TradeStatus.COMPLETE);
  }
  @Test
  void 거래완료_판매자() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    chatRoom.setTradeSellerComplete(true);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.completeTrade(1L, seller);
    // Then
    assertThat(chatRoom.isTradeBuyerComplete()).isFalse();
    assertThat(chatRoom.isTradeSellerComplete()).isTrue();
    assertThat(chatRoom.getTradeStatus()).isNotEqualTo(TradeStatus.COMPLETE);
  }
  @Test
  void 거래완료_양쪽_모두_완료() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);
    chatRoom.setTradeBuyerComplete(true);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.completeTrade(1L, seller);
    // Then
    assertThat(chatRoom.isTradeBuyerComplete()).isTrue();
    assertThat(chatRoom.isTradeSellerComplete()).isTrue();
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.COMPLETE);
  }
  @Test
  void 거래완료_권한없음() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    Member anonymous =  new Member("test@test", "누굴까요");
    anonymous.setId(9L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When & Then
    assertThatThrownBy(() -> chatroomService.completeTrade(1L, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("거래 완료 권한이 없습니다.");

    then(chatRoomRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  void 거래취소_구매자() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.cancelTrade(1L, buyer);
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.CANCEL);
    verify(post).setStatus(TradeStatus.CANCEL);
  }
  @Test
  void 거래취소_판매자() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.cancelTrade(1L, seller);
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.CANCEL);
    verify(post).setStatus(TradeStatus.CANCEL);
  }
  @Test
  void 거래취소_이미취소된거래_예외발생() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.cancelTrade(1L, seller);
    // Then
    assertThatThrownBy(() -> chatroomService.cancelTrade(1L, buyer))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 거래가 취소되었습니다.");

    verify(post).setStatus(TradeStatus.CANCEL);
  }
  @Test
  void 거래취소_권한없음() {
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    Member anonymous =  new Member("test@test", "누굴까요");
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When & Then
    assertThatThrownBy(() -> chatroomService.cancelTrade(1L, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("권한이 없습니다.");
  }
  @Test
  void 거래재시도_성공(){
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);
    chatRoom.setTradeStatus(TradeStatus.CANCEL);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When
    chatroomService.reopenTrade(1L, buyer);
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.TRADE);
    assertThat(chatRoom.isTradeSellerComplete()).isFalse();
    assertThat(chatRoom.isTradeBuyerComplete()).isFalse();
    verify(post).setStatus(TradeStatus.TRADE);
  }
  @Test
  void 거래재시도_취소된거래가아닐경우_예외발생(){
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When & Then
    assertThatThrownBy(() -> chatroomService.reopenTrade(1L, buyer))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("취소된 거래만 재거래가 가능합니다.");

  }
  @Test
  void 거래재시도_권한없음(){
    // Given
    Member buyer =  new Member("test@test", "구매자");
    buyer.setId(1L);
    Member seller =  new Member("test@test", "판매자");
    seller.setId(2L);
    Member anonymous =  new Member("test@test", "누굴까요");
    ItemPost post = mock(ItemPost.class);
    ChatRoom chatRoom = ChatRoom.of(
        buyer,
        seller,
        post
    );
    chatRoom.setId(1L);

    given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
    // When & Then
    assertThatThrownBy(() -> chatroomService.reopenTrade(1L, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("권한이 없습니다.");
  }
}