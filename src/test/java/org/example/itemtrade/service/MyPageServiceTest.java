package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.Util.SoftDeleteUtil;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemImageRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("비즈니스 로직 - 마이페이지")
@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

  @InjectMocks
  private MyPageService myPageService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ItemPostRepository itemPostRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ItemImageRepository itemImageRepository;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Test
  @DisplayName("내 프로필 보기 성공")
  void getMyPosts() {
    // Given
    Member member = Member.builder()
        .email("test@test.com")
        .nickName("테스트")
        .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg")
        .introduction("안녕하세요")
        .build();
    member.setId(1L);
    List<ItemPost> posts = List.of(ItemPost.builder()
        .id(1L)
        .title("테스트 게시글")
        .description("테스트 게시글 내용")
        .price(10000)
        .category(Category.RPG)
        .isSold(false)
        .seller(member)
        .build());
    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(itemPostRepository.findAllBySeller(member)).thenReturn(List.of());
    when(itemPostRepository.findAllBySeller(member)).thenReturn(posts);
    // When
    List<ItemPostResponse> result = myPageService.getMyPosts(member);
    // Then
    assertThat(result.get(0).title()).isEqualTo("테스트 게시글");
    assertThat(result.get(0).description()).isEqualTo("테스트 게시글 내용");
    assertThat(result.get(0).price()).isEqualTo(10000);
    assertThat(result.get(0).category()).isEqualTo(Category.RPG);
    assertThat(result).hasSize(1);
  }
  @Test
  @DisplayName("내 프로필 보기 실패 - 회원을 찾을 수 없음")
  void getMyPostsFail() {
    // Given
    Member member = Member.builder()
        .email("test@test.com")
        .nickName("테스트")
        .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg")
        .introduction("안녕하세요")
        .build();
    member.setId(1L);
    when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
    // When & Then
    assertThatThrownBy(() -> myPageService.getMyPosts(member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("사용자를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("회원 탈퇴 성공")
  void deleteMember() {
    // Given
    Member member = Member.builder()
        .id(1L)
        .email("test@test.com")
        .nickName("테스트")
        .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg")
        .introduction("안녕하세요")
        .build();
    List<ItemPost> posts = List.of(ItemPost.builder()
        .id(1L)
        .title("테스트 게시글")
        .description("테스트 게시글 내용")
        .price(10000)
        .category(Category.RPG)
        .isSold(false)
        .seller(member)
        .build());
    List<Comment> comments = List.of(Comment.builder()
        .id(1L)
        .content("테스트 댓글")
        .itemPost(posts.get(0))
        .writer(member)
        .build());
    List<ItemImage> itemImages = List.of(ItemImage.builder()
        .id(1L)
        .imagePath("https://example.com/image.jpg")
        .itemPost(posts.get(0))
        .build());
    List<ChatRoom> chatRooms = List.of(ChatRoom.builder()
        .id(1L)
        .buyer(member)
        .seller(member)
        .build());
    List<ChatMessage> chatMessages = List.of(ChatMessage.builder()
        .id(1L)
        .content("테스트 메시지")
        .sender(member)
        .chatRoom(chatRooms.get(0))
        .build());
    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(itemPostRepository.findAllBySeller(member)).thenReturn(posts);
    when(itemImageRepository.findAllByItemPostIn(posts)).thenReturn(itemImages);
    when(commentRepository.findAllByWriter(member)).thenReturn(comments);
    when(chatMessageRepository.findAllBySender(member)).thenReturn(chatMessages);
    when(chatRoomRepository.findAllByBuyerAndSeller(member, member)).thenReturn(chatRooms);

    // When
    try (MockedStatic<SoftDeleteUtil> softDeleteUtilMockedStatic =mockStatic(SoftDeleteUtil.class)){
      myPageService.deleteMember(member);

      // Then
      softDeleteUtilMockedStatic.verify(() -> SoftDeleteUtil.softDeleteAll(posts));
      softDeleteUtilMockedStatic.verify(() -> SoftDeleteUtil.softDeleteAll(comments));
      softDeleteUtilMockedStatic.verify(() -> SoftDeleteUtil.softDeleteAll(itemImages));
      softDeleteUtilMockedStatic.verify(() -> SoftDeleteUtil.softDeleteAll(chatMessages));
      softDeleteUtilMockedStatic.verify(() -> SoftDeleteUtil.softDeleteAll(chatRooms));

      assertTrue(member.isDeleted());
      assertThat(member.getId()).isEqualTo(1L);
    }
  }
}