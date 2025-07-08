package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.example.itemtrade.dto.User.UserType;
import org.example.itemtrade.dto.request.MemberUpdateRequest;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemImageRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.LikeRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.example.itemtrade.service.MyPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest( properties = {
    "file.upload-dir=./build/uploads-test",
    "file.chat-dir=./build/uploads-test"
})
@AutoConfigureMockMvc
@Transactional
class MyPageControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ItemPostRepository itemPostRepository;
  @Autowired
  private MemberBlockRepository memberBlockRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private ItemImageRepository itemImageRepository;
  @Autowired
  private ChatRoomRepository chatRoomRepository;
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private ChatMessageRepository chatMessageRepository;
  @Autowired
  private MyPageService myPageService;

  @Test
  void 마이페이지() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    Member blocked = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    MemberBlock memberBlock = MemberBlock.builder()
        .blocker(member)
        .blocked(blocked)
        .build();
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("test")
        .description("test")
        .price(1000)
        .seller(member)
        .build());
    memberBlockRepository.save(memberBlock);
    UserType user = new TestUserType(member, "FORM");
    // When & Then
    mockMvc.perform(get("/mypage")
            .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
        .andExpect(model().attributeExists("posts"))
        .andExpect(model().attributeExists("loginType"))
        .andExpect(model().attributeExists("member"))
        .andExpect(model().attributeExists("blockedMembers"))
        .andExpect(view().name("mypage"))
        .andExpect(status().isOk());
  }

  @Test
  void 정보수정() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .introduction("test")
        .build());
    UserType user = new TestUserType(member, "FORM");
    MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("updatedUser", "updatedImageUrl", "updatedIntroduction");
    // When
    mockMvc.perform(post("/mypage/update").with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of())))
            .param("nickName", memberUpdateRequest.getNickName())
            .param("profileImageUrl", memberUpdateRequest.getProfileImageUrl())
            .param("introduction", memberUpdateRequest.getIntroduction())
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/mypage"));
    // Then
    Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(updatedMember.getNickName()).isEqualTo(memberUpdateRequest.getNickName());
    assertThat(updatedMember.getIntroduction()).isEqualTo(memberUpdateRequest.getIntroduction());
    assertThat(updatedMember.getProfileImageUrl()).isEqualTo(memberUpdateRequest.getProfileImageUrl());
  }

  @Test
  void 차단해제() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    Member blocked = memberRepository.save(Member.builder()
        .email("test2@test.com")
        .password("password")
        .nickName("testUser2")
        .build());
    MemberBlock memberBlock = MemberBlock.builder()
        .blocker(member)
        .blocked(blocked)
        .build();
    memberBlockRepository.save(memberBlock);
    UserType user = new TestUserType(member, "FORM");
    // When
    mockMvc.perform(post("/unblock/" + blocked.getId())
        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/mypage"));
    // Then
    assertThat(memberBlockRepository.existsByBlockerAndBlocked(member, blocked)).isFalse();
  }

  @Test
  void 회원탈퇴_url() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    UserType user = new TestUserType(member, "FORM");
    // When & Then
    mockMvc.perform(post("/mypage/delete").with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/logout"));
  }
  @Test
  void 회원탈퇴_service() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@test.com")
        .password("password")
        .nickName("testUser")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("test")
        .description("test")
        .price(1000)
        .seller(member)
        .build());
    ItemImage image = itemImageRepository.save(ItemImage.builder()
        .imagePath("/test")
        .itemPost(post)
        .build());
    Comment comment = commentRepository.save(Comment.builder()
        .content("test")
        .itemPost(post)
        .writer(member)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .buyer(member)
        .seller(member2)
        .build());
    // When
    myPageService.deleteMember(member);
    // Then
    assertThat(memberRepository.findById(member.getId()).orElseThrow().isDeleted()).isTrue();
    assertThat(itemPostRepository.findById(post.getId()).orElseThrow().isDeleted()).isTrue();
    assertThat(itemImageRepository.findById(image.getId()).orElseThrow().isDeleted()).isTrue();
    assertThat(commentRepository.findById(comment.getId()).orElseThrow().isDeleted()).isTrue();
    assertThat(chatRoomRepository.findById(chatRoom.getId()).orElseThrow().isDeleted()).isFalse(); // 채팅방은 양쪽에서 삭제를 해야 True가 됨
  }
}