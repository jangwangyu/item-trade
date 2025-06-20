package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.User.CustomOAuth2User;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ItemPostRepository itemPostRepository;

  @Autowired
  private MockMvc mock;

  @Test
  void 게시글_조회() throws Exception {
    // Given
    Member member = memberRepository.save(
        Member.builder()
            .email("test@test.com")
            .nickName("테스트")
            .build()
    );

    CustomOAuth2User customuser = new CustomOAuth2User(member, Map.of());

    ItemPost post = itemPostRepository.save(
        ItemPost.builder()
            .title("테스트 게시글")
            .description("테스트 내용")
            .price(10000)
            .category(Category.RPG)
            .seller(member)
            .build()
    );
    // When
    mock.perform(get("/posts/" + post.getId())
        .with(authentication(new UsernamePasswordAuthenticationToken(customuser,null, customuser.getAuthorities())))
    )
    // Then
        .andExpect(status().isOk())
        .andExpect(view().name("detail"))
        .andExpect(model().attributeExists("post"))
        .andExpect(model().attributeExists("isAuthor"))
        .andExpect(model().attributeExists("comments"))
        .andExpect(model().attribute("currentUserId", member.getId()));
  }

  @Test
  void 게시글_생성_Post() throws Exception {
    // Given
    Member member = memberRepository.save(
        Member.builder()
            .email("test@test.com")
            .nickName("테스트")
            .build()
    );

    CustomOAuth2User customuser = new CustomOAuth2User(member, Map.of());

    MockMultipartFile mockFile = new MockMultipartFile("images", "image.jpg", "image/jpeg", "image".getBytes());
    // When
    mock.perform(multipart("/posts")
            .file(mockFile)
            .param("title", "테스트 게시글")
            .param("description", "테스트 내용")
            .param("price", "10000")
            .param("category", Category.RPG.name())
            .with(authentication(new UsernamePasswordAuthenticationToken(customuser, null, customuser.getAuthorities())))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));
    // Then
    assertThat(itemPostRepository.findAll()).hasSize(1);
  }

  @Test
  void 게시글_수정_Post() throws Exception {
    // Given
    Member member = memberRepository.save(
        Member.builder()
            .email("test@test.com")
            .nickName("테스트")
            .build()
    );

    CustomOAuth2User customuser = new CustomOAuth2User(member, Map.of());

    ItemPost post = itemPostRepository.save(
        ItemPost.builder()
            .title("테스트 게시글")
            .description("테스트 내용")
            .price(10000)
            .category(Category.RPG)
            .seller(member)
            .build()
    );
    // 기존 이미지
    ItemImage image = ItemImage.of("/uploads/post/existing-image.jpg");
    image.setId(1L); // 임시 ID 설정
    post.addImage(image);

    MockMultipartFile newImage = new MockMultipartFile("images", "new-image.jpg", "image/jpeg", "new image".getBytes());
    // When
    mock.perform(multipart("/posts/edit/" + post.getId())
            .file(newImage)
            .param("title", "수정된 게시글")
            .param("description", "수정된 내용")
            .param("price", "20000")
            .param("category", Category.ACTION.name())
            .param("deletedItemImageIds", image.getId().toString()) // 기존 이미지 삭제
            .with(request -> {
                request.setMethod("PUT");
                return request;
            })
            .with(authentication(new UsernamePasswordAuthenticationToken(customuser, null, customuser.getAuthorities())))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/posts/" + post.getId()));
    // Then
    ItemPost updatedPost = itemPostRepository.findById(post.getId()).orElseThrow();
    assertThat(updatedPost.getTitle()).isEqualTo("수정된 게시글");
    assertThat(updatedPost.getDescription()).isEqualTo("수정된 내용");
    assertThat(updatedPost.getPrice()).isEqualTo(20000);
    assertThat(updatedPost.getCategory()).isEqualTo(Category.ACTION);
    assertThat(updatedPost.getImages()).hasSize(1); // 새로운 이미지가 추가되고 기존 이미지는 삭제됨
  }

  @Test
  void 게시글_삭제() throws Exception {
    // Given
    Member member = memberRepository.save(
        Member.builder()
            .email("test@test.com")
            .nickName("테스트")
            .build()
    );

    CustomOAuth2User customuser = new CustomOAuth2User(member, Map.of());

    ItemPost post = itemPostRepository.save(
        ItemPost.builder()
            .title("테스트 게시글")
            .description("테스트 내용")
            .price(10000)
            .category(Category.RPG)
            .seller(member)
            .build()
    );
    ItemImage image = ItemImage.of("/uploads/post/existing-image.jpg");
    post.addImage(image);
    // When
    mock.perform(post("/posts/" + post.getId() + "/delete")

            .with(authentication(new UsernamePasswordAuthenticationToken(customuser, null, customuser.getAuthorities())))
            .with(request -> {
                request.setMethod("DELETE");
                return request;
            }))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));

    // Then
    boolean exists = itemPostRepository.findById(post.getId()).isPresent();
    assertThat(exists).isFalse(); // 게시글이 삭제되었는지 확인
  }
}