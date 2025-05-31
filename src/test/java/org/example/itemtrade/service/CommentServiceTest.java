package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.request.CommentCreateRequest;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ItemPostRepository itemPostRepository;

  @Mock
  private MemberRepository memberRepository;

  @Test
  void 댓글리스트() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    Comment comment1 = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();

    Comment comment2 = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test2")
        .build();
    Pageable pageable = PageRequest.of(0, 10);
    List<Comment> comments = List.of(comment1, comment2);
    Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

    when(itemPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
    when(commentRepository.findAllByItemPostOrderByCreatedAtDesc(post,pageable)).thenReturn(commentPage);
    // when
    Page<CommentDto> result = commentService.commentList(post.getId(),pageable);
    // then
    assertThat(result).hasSize(2);
  }
  @Test
  void 댓글달기() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    CommentCreateRequest request = new CommentCreateRequest("test");

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);

    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(itemPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    // When
    CommentDto result = commentService.addComment(request, post.getId(), member);

    // Then
    assertThat(result.content()).isEqualTo("test");
    assertThat(result.writerNickname()).isEqualTo(member.getNickName());
  }

  @Test
  void 댓글달기_게시물없음() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    CommentCreateRequest request = new CommentCreateRequest("test");

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);

    // When & Then
    assertThatThrownBy(() -> commentService.addComment(request, 999L, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("게시물이 존재하지 않습니다.");
  }

  @Test
  void 댓글달기_작성자_없음() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);
    Member anonymous = new Member("test@test", "완구");
    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    CommentCreateRequest request = new CommentCreateRequest("test");

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);

    when(itemPostRepository.findById(post.getId())).thenReturn(Optional.of(post));

    // When & Then
    assertThatThrownBy(() -> commentService.addComment(request, 1L, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("작성자가 존재하지 않습니다.");
  }

  @Test
  void 댓글삭제_성공() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

    // when
    commentService.deleteComment(comment.getId(), member);

    // Then
    verify(commentRepository).delete(comment);
  }

  @Test
  void 댓글삭제_댓글존재하지않음() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);

    // when & Then
    assertThatThrownBy(() -> commentService.deleteComment(999L, member))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("댓글이 존재하지 않습니다.");
  }

  @Test
  void 댓글삭제_작성자가아닐경우() {
    // Given
    Member member = new Member("test@test", "완구");
    member.setId(1L);
    Member anonymous = new Member("test@test", "완구");

    ItemPost post = ItemPost.builder()
        .title("title")
        .description("desc")
        .price(1000)
        .category(Category.RPG)
        .seller(member)
        .build();
    post.setId(1L);

    Comment comment = Comment.builder()
        .writer(member)
        .itemPost(post)
        .content("test")
        .build();
    comment.setId(1L);

    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    // when & Then
    assertThatThrownBy(() -> commentService.deleteComment(1L, anonymous))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("댓글 작성자만 삭제할 수 있습니다.");

    verify(commentRepository, never()).delete(any());
  }
}