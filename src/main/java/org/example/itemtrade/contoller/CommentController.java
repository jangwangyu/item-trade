package org.example.itemtrade.contoller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.request.CommentCreateRequest;
import org.example.itemtrade.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/comments")
@RestController
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/{postId}")
  public ResponseEntity<Page<CommentDto>> getComments(@PathVariable(value = "postId") Long postId, @PageableDefault(size = 5, sort = "createdAt", direction = DESC) Pageable pageable) {

    Page<CommentDto> comments = commentService.commentList(postId, pageable);
    return ResponseEntity.ok(comments);
  }

  // 댓글 작성
  @PostMapping("/{postId}")
  public ResponseEntity<CommentDto> addComment(
      @PathVariable(value = "postId") Long postId,
      @AuthenticationPrincipal(expression = "member") Member member,
      @RequestBody CommentCreateRequest request) {

    CommentDto comment = commentService.addComment(request, postId, member);
    return ResponseEntity.ok(comment);
  }

  // 댓글 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable(value = "commentId") Long commentId, @AuthenticationPrincipal(expression = "member") Member member) {

    commentService.deleteComment(commentId, member);
    return ResponseEntity.noContent().build();
  }
}
