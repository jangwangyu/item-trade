package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.request.CommentCreateRequest;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.example.itemtrade.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest( properties = {
    "file.upload-dir=./build/uploads-test",
    "file.chat-dir=./build/uploads-test"
})
@Transactional
class CommentControllerTest {

   @Autowired
   private CommentRepository commentRepository;
   @Autowired
   private ItemPostRepository itemPostRepository;
   @Autowired
   private MemberRepository memberRepository;
   @Autowired
   private CommentService commentService;


   @Test
   void 댓글작성() {
     // Given
     Member member = memberRepository.save(Member.builder()
         .email("test@test.com")
         .nickName("testUser")
         .build());
     ItemPost post = itemPostRepository.save(ItemPost.builder()
          .title("Test Post")
          .description("This is a test post.")
          .seller(member)
         .build());
     CommentCreateRequest request = new CommentCreateRequest("test");
     // When
     CommentDto result = commentService.addComment(request, post.getId(), member);
     // Then
     assertThat(result.content()).isEqualTo("test");
   }

   @Test
   void 댓글삭제() {
     // Given
     Member member = memberRepository.save(Member.builder()
         .email("test@test.com")
         .nickName("testUser")
         .build());
     ItemPost post = itemPostRepository.save(ItemPost.builder()
         .title("Test Post")
         .description("This is a test post.")
         .seller(member)
         .build());
     Comment comment = commentRepository.save(Comment.builder()
         .writer(member)
         .itemPost(post)
         .content("test comment")
         .build());
     // When
     commentService.deleteComment(comment.getId(), member);
      // Then
     boolean exists = commentRepository.findById(comment.getId()).isPresent();
     assertThat(exists).isFalse();
   }

   @Test
   void 댓글_삭제_작성자가아닐경우_예외(){
     // Given
     Member member = memberRepository.save(Member.builder()
         .email("test@test.com")
         .nickName("testUser")
         .build());
     Member anonymous = memberRepository.save(Member.builder()
         .email("test@test.com")
         .nickName("testUser")
         .build());
     ItemPost post = itemPostRepository.save(ItemPost.builder()
         .title("Test Post")
         .description("This is a test post.")
         .seller(member)
         .build());
     Comment comment = commentRepository.save(Comment.builder()
         .writer(member)
         .itemPost(post)
         .content("test comment")
         .build());
     // When & Then
     assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), anonymous))
         .isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("댓글 작성자만 삭제할 수 있습니다.");
   }
}