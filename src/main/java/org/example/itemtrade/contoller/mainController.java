package org.example.itemtrade.contoller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.response.UserInfoResponse;
import org.example.itemtrade.service.LikesService;
import org.example.itemtrade.service.MemberService;
import org.example.itemtrade.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class mainController {

  private final PostService postService;
  private final MemberService memberService;
  private final LikesService likesService;

  @GetMapping("/api/user/me")
  public UserInfoResponse index(@AuthenticationPrincipal(expression = "member") Member member) {

    return new UserInfoResponse(member.getEmail(), member.getNickName());
  }

  // 게시글 좋아요
  @PostMapping("/likes/{id}")
  @ResponseBody
  public ResponseEntity<?> toggleLikes(@PathVariable Long id, @AuthenticationPrincipal(expression = "member") Member member) {
    Member currentMember = memberService.getMemberId(member.getId());
    ItemPost post = postService.PostById(id);

    boolean liked = likesService.toggleLikes(currentMember, post);
    int likeCount = post.getLikeCount();

    return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));

  }

  @GetMapping("/chat")
  public String chat() {
    return "chat";
  }

  @GetMapping("/detail")
  public String detail() {
    return "detail";
  }
}
