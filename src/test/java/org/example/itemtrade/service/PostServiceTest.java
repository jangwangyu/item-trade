package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.ItemPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("비즈니스 로직 - 게시물")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock
  private ItemPostRepository postRepository;

  @InjectMocks
  private PostService postService;

  @Test
  void 모든_게시물_확인() {
    // given
    Member member = new Member("test@test.com", "완구");
    ItemPost post1 = ItemPost.builder()
        .seller(member)
        .title("title1")
        .description("desc1")
        .price(1000)
        .category(Category.RPG)
        .build();
    ItemPost post2 = ItemPost.builder()
        .seller(member)
        .title("title2")
        .description("desc2")
        .price(2000)
        .category(Category.RPG)
        .build();
    // when
    Mockito.when(postRepository.findAll()).thenReturn(List.of(post1, post2));

    List<ItemPostResponse> result = postService.getAllPosts(null, null, null);
    // then
    assertThat(result).hasSize(2);
    verify(postRepository).findAll();
  }

  @Test
  void 게시물_단건_조회() {
    // given
    Member member = new Member("test@test.com", "완구");
    ItemPost post = ItemPost.builder().seller(member).title("title1").description("desc1").price(1000).category(Category.RPG).build();
    Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    // when
    ItemPostResponse result = postService.getPostById(1L);

    // then
    assertThat(result.sellerNickname()).isEqualTo("완구");
    assertThat(result.title()).isEqualTo("title1");
    assertThat(result.description()).isEqualTo("desc1");
    assertThat(result.price()).isEqualTo(1000);
    assertThat(result.category()).isEqualTo(Category.RPG);
  }

  @Test
  void 게시물_생성() throws IOException {
    // given
    Member member = new Member("test@test.com", "완구");

    ItemPostCreateRequest request = new ItemPostCreateRequest("title1", "desc1", 1000, Category.RPG);
    MockMultipartFile image = new MockMultipartFile("imageUrl", "test.jpg", "image/jpeg", "image-content".getBytes());

    // when
    Mockito.when(postRepository.save(any(ItemPost.class))).thenAnswer(inv -> inv.getArgument(0));

    ItemPostResponse response = postService.createPost(request, member, image);

    // then
    assertThat(response.title()).isEqualTo("title1");
  }

  @Test
  void 게시물_수정() throws IOException {
    // given
    Member member = new Member("test@test.com", "완구");
    member.setId(1L);
    ItemPost post = ItemPost.builder().title("title1").description("desc1").price(1000).category(
        Category.ACTION).seller(member).build();

    Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    ItemPostUpdateRequest request = new ItemPostUpdateRequest("updated", "desc2", 2000, Category.ACTION);
    MockMultipartFile image = new MockMultipartFile("imageUrl", "new.jpg", "image/jpeg", "image-data".getBytes());

    // when
    postService.updatePost(1L, request, member, image);

    // then
    assertThat(post.getTitle()).isEqualTo("updated");
    assertThat(post.getPrice()).isEqualTo(2000);
  }

  @Test
  void 게시물_수정_권한_없음() {
    // given
    Member test1 = new Member("author@test.com", "저자");
    Member test2 = new Member("stranger@test.com", "남");
    test1.setId(1L);
    test2.setId(2L);
    ItemPost post = ItemPost.builder().title("title").description("desc").price(1000).category(Category.FPS).seller(test1).build();

    Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    // when
    ItemPostUpdateRequest request = new ItemPostUpdateRequest("updated", "desc2", 2000, Category.FPS);
    MockMultipartFile image = new MockMultipartFile("imageUrl", "file.jpg", "image/jpeg", "data".getBytes());

    // then
    assertThrows(AccessDeniedException.class, () -> postService.updatePost(1L, request, test2, image));
  }

  @Test
  void 게시물_삭제() {
    // given
    Member member = new Member("test@test.com", "완구");
    member.setId(1L);
    ItemPost post = ItemPost.builder().title("title1").description("desc1").price(1000).category(Category.FPS).seller(member).imagePath("/uploads/test.jpg").build();

    Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    // when
    postService.deletePost(1L, member);

    // then
    verify(postRepository).delete(post);
  }

  @Test
  void 게시물_수정용_조회() {
    // given
    Member member = new Member("test@test.com", "완구");
    member.setId(1L);
    ItemPost post = ItemPost.builder().seller(member).title("수정용").description("desc").price(1000).category(Category.FPS).build();
    Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    // when
    ItemPostResponse response = postService.getPostForEdit(1L);

    // then
    assertThat(response.title()).isEqualTo("수정용");
  }
}
