package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // when
    when(itemPostRepository.findById(post.getId())).thenReturn(Optional.of(post));
    when(commentRepository.findAllByItemPostOrderByCreatedAtDesc(post)).thenReturn(
        List.of(comment1, comment2));

    List<CommentDto> result = commentService.commentList(post.getId());
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


    // Mocking
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
  void 댓글삭제() {
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
}