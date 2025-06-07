package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
@ActiveProfiles("test")
@DisplayName("비즈니스 로직 - 게시물")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock
  private ItemPostRepository postRepository;
  @Mock
  private MemberBlockRepository memberBlockRepository;

  @InjectMocks
  private PostService postService;

  @Test
  void 모든_게시물_확인() {
    // given
    Member member = new Member("test@test.com", "완구");
    member.setId(1L);
    ItemPost post1 = ItemPost.builder()
        .seller(member)
        .title("title1")
        .description("desc1")
        .price(1000)
        .category(Category.RPG)
        .build();
    int totalCount = 10;
    Pageable pageable = PageRequest.of(0, 10);
    Page<ItemPost> postPage = new PageImpl<>(List.of(post1), pageable, totalCount);
    when(postRepository.findAllPosts(any(),any(),any(),any(),any())).thenReturn(postPage);
    when(memberBlockRepository.findAllByBlocker(any(Member.class))).thenReturn(List.of());
    // when
    Page<ItemPostResponse> result = postService.getAllPosts(post1.getCategory().toString(), 0, 99999, member, Pageable.ofSize(1));
    // then
    assertThat(result).hasSize(1);
    verify(postRepository).findAllPosts(any(),any(),any(),any(),any());
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
    List<MultipartFile> images = List.of(image);

    // when
    Mockito.when(postRepository.save(any(ItemPost.class))).thenAnswer(inv -> inv.getArgument(0));

    ItemPostResponse response = postService.createPost(request, member, images);

    // then
    assertThat(response.title()).isEqualTo("title1");
    assertThat(response.description()).isEqualTo("desc1");
    assertThat(response.price()).isEqualTo(1000);
    assertThat(response.category()).isEqualTo(Category.RPG);
    assertThat(response.imagePaths()).hasSize(1);
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
    List<MultipartFile> images = List.of(image);
    ItemImage imageToDelete = ItemImage.builder().id(1L).imagePath("a.jpg").build();  // 삭제 대상
    ItemImage imageToKeep = ItemImage.builder().id(2L).imagePath("b.jpg").build();    // 유지 대상
    List<Long> deletedItemImageIds = List.of(imageToDelete.getId()); // 1L
    post.addImage(imageToDelete);
    post.addImage(imageToKeep);
    // when
    postService.updatePost(1L, request, member, images, deletedItemImageIds);

    // then
    assertThat(post.getTitle()).isEqualTo("updated");
    assertThat(post.getPrice()).isEqualTo(2000);
    assertThat(post.getImages()).hasSize(2);
  }

  @Test
  void 게시물_수정_권한_없음() {
    // given
    Member test1 = new Member("author@test.com", "저자");
    Member test2 = new Member("stranger@test.com", "남");
    test1.setId(1L);
    test2.setId(2L);
    ItemPost post = ItemPost.builder().title("title").description("desc").price(1000).category(Category.FPS).seller(test1).build();

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    ItemImage imageToDelete = ItemImage.builder().id(1L).imagePath("a.jpg").build();  // 삭제 대상
    ItemImage imageToKeep = ItemImage.builder().id(2L).imagePath("b.jpg").build();    // 유지 대상
    List<Long> deletedItemImageIds = List.of(imageToDelete.getId()); // 1L
    post.addImage(imageToDelete);
    post.addImage(imageToKeep);

    // when
    ItemPostUpdateRequest request = new ItemPostUpdateRequest("updated", "desc2", 2000, Category.FPS);
    MockMultipartFile image = new MockMultipartFile("imageUrl", "file.jpg", "image/jpeg", "data".getBytes());
    List<MultipartFile> images = List.of(image);

    // then
    assertThrows(AccessDeniedException.class, () -> postService.updatePost(1L, request, test2, images, deletedItemImageIds));
  }

  @Test
  void 게시물_삭제() {
    // given
    Member member = new Member("test@test.com", "완구");
    member.setId(1L);
    ItemPost post = ItemPost.builder().title("title").description("desc").price(1000).category(Category.FPS).seller(member).build();
    ItemImage image = ItemImage.builder().id(1L).imagePath("a.jpg").build(); // 이미지
    post.addImage(image);
    when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    // when & then
    try(MockedStatic<Files> filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.deleteIfExists(any())).thenReturn(true);
      postService.deletePost(1L, member);

      filesMock.verify(() -> Files.deleteIfExists(any(Path.class)),times(1));
    }
  }

  @Test
  void 게시물_삭제_권한_없음() {
    // Given
    Member member = new Member("test@test.com", "완구");
    Member member2 = new Member("test2@test.com", "asd");
    member.setId(1L);
    member2.setId(2L);
    ItemPost post = ItemPost.builder().title("title").description("desc").price(1000).category(Category.FPS).seller(member).build();
    post.setId(1L);

    when(postRepository.findById(1L)).thenReturn(Optional.of(post));
    // When
    postService.deletePost(1L, member);
    // Then
    assertThatThrownBy(() -> postService.deletePost(1L, member2))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("게시글 작성자만 삭제할 수 있습니다.");
  }
}
