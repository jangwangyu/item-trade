package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.User.UserType;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.example.itemtrade.service.ChatroomService;
import org.example.itemtrade.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest( properties = {
    "file.upload-dir=./build/uploads-test",
    "file.chat-dir=./build/uploads-test"
})
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatRoomControllerTest {
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ChatRoomRepository chatRoomRepository;
  @Autowired
  private ItemPostRepository itemPostRepository;
  @Autowired
  private MemberBlockRepository memberBlockRepository;
  @Autowired
  private ChatroomService chatroomService;
  @Autowired
  private MemberService memberService;
  @Autowired
  private MockMvc mvc;

  @Test
  void 채팅방목록() throws Exception {
    // Given
    Member member = memberRepository.save(Member.builder()
            .email("test@124.com")
            .password("password")
            .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
            .email("test@421.com")
            .password("password")
            .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
            .title("title")
            .description("description")
            .seller(member)
            .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
            .itemPost(post)
            .title("Test Chat Room")
            .seller(member)
            .buyer(member2)
            .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(member, "FORM");
    // When & Then
    mvc.perform(get("/chat-list").with(authentication(new UsernamePasswordAuthenticationToken(user, null,
        List.of()))))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("chatRooms"));
  }

  @Test
  void 채팅방_생성_service() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@412.com")
        .password("password")
        .nickName("test")
        .build());
    Member buyer = memberRepository.save(Member.builder()
        .email("test@532.com")
        .password("password")
        .nickName("test")
        .build());
    UserType user = new TestUserType(buyer, "FORM");
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member)
        .price(10000)
        .build());
    // When
    ChatRoom chatRoom = chatroomService.createChatRoom(user.getMember(), post.getId());
    // Then
    assertThat(chatRoom.getBuyer()).isEqualTo(user.getMember());
    assertThat(chatRoom.getSeller()).isEqualTo(member);
    assertThat(chatRoom.getItemPost()).isEqualTo(post);
  }

  @Test
  void 채팅방_생성_controller() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@325.com")
        .password("password")
        .nickName("test")
        .build());
    Member buyer = memberRepository.save(Member.builder()
        .email("test@643.com")
        .password("password")
        .nickName("test")
        .build());
    UserType user = new TestUserType(buyer, "FORM");
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member)
        .price(10000)
        .build());
    // When & Then
    mvc.perform(post("/chat/" + post.getId())
        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("/chat/*"));
  }

  @Test
  void 채팅방_조회() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@436.com")
        .password("password")
        .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@456.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(member)
        .buyer(member2)
        .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(member, "FORM");
    // When & Then
    mvc.perform(get("/chat/" + chatRoom.getId())
        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("room"))
        .andExpect(model().attributeExists("messages"))
        .andExpect(model().attributeExists("chatRoomId"))
        .andExpect(model().attributeExists("currentUserId"))
        .andExpect(model().attributeExists("isSeller"))
        .andExpect(model().attributeExists("isBuyer"));
  }

  @Test
  void 채팅방_삭제_service() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@546.com")
        .password("password")
        .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@765.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member2)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(member2)
        .buyer(member)
        .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(member, "FORM");
    // When
    chatroomService.deleteChatRoom(post.getId(), user.getMember());
    // Then
    assertThat(chatRoom.getBuyer()).isEqualTo(user.getMember());
    assertThat(chatRoom.getSeller()).isEqualTo(member2);
    assertThat(chatRoom.isDeletedByBuyer()).isTrue();
  }

  @Test
  void 회원차단_service() throws Exception{
    // Given
    Member blocker = memberRepository.save(Member.builder()
        .email("test@657.com")
        .password("password")
        .nickName("test")
        .build());
    Member blocked = memberRepository.save(Member.builder()
        .email("test@876.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(blocker)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(blocker)
        .buyer(blocked)
        .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(blocker, "FORM");
    // When
    memberService.blockMember(blocker, blocked);
    // Then
    assertThat(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)).isTrue();
  }

  @Test
  void 거래완료_service() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@687.com")
        .password("password")
        .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@978.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member2)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(member2)
        .buyer(member)
        .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(member, "FORM");
    UserType user2 = new TestUserType(member2, "FORM");
    // When
    chatroomService.completeTrade(post.getId(), user.getMember());
    chatroomService.completeTrade(post.getId(), user2.getMember());
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.COMPLETE); // 양쪽 전부 거래완료를 해야 COMPLEATE 상태가 된다.
  }

  @Test
  void 거래취소_service() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@09.com")
        .password("password")
        .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@89.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member2)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(member2)
        .buyer(member)
        .build());
    chatRoom.setTradeStatus(TradeStatus.TRADE);
    UserType user = new TestUserType(member, "FORM");
    // When
    chatroomService.cancelTrade(post.getId(), user.getMember());
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.CANCEL);
  }

  @Test
  void 재거래_service() throws Exception{
    // Given
    Member member = memberRepository.save(Member.builder()
        .email("test@te1st.com")
        .password("password")
        .nickName("test")
        .build());
    Member member2 = memberRepository.save(Member.builder()
        .email("test@tes2t.com")
        .password("password")
        .nickName("test")
        .build());
    ItemPost post = itemPostRepository.save(ItemPost.builder()
        .title("title")
        .description("description")
        .seller(member2)
        .price(10000)
        .build());
    ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
        .itemPost(post)
        .title("Test Chat Room")
        .seller(member2)
        .buyer(member)
        .build());
    chatRoom.setTradeStatus(TradeStatus.CANCEL);
    UserType user = new TestUserType(member, "FORM");
    // When
    chatroomService.reopenTrade(post.getId(), user.getMember());
    // Then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.TRADE);
  }

}