package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.request.CommentCreateRequest;
import org.example.itemtrade.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성
  @PostMapping("/{postId}")
  public String addComment(CommentCreateRequest request, @PathVariable Long postId, @AuthenticationPrincipal(expression = "member") Member member) {
    commentService.addComment(request, postId, member);
    return "redirect:/posts/" + postId;
  }

  // 댓글 삭제
  @DeleteMapping("/{commentId}/delete")
  public String deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal(expression = "member") Member member) {

    Long postId = commentService.deleteComment(commentId, member);
    return "redirect:/posts/" + postId;
  }
}
