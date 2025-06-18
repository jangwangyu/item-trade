package org.example.itemtrade.contoller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.User.CustomOAuth2User;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.service.LikesService;
import org.example.itemtrade.service.MemberService;
import org.example.itemtrade.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class mainController {

  private final PostService postService;
  private final MemberService memberService;
  private final LikesService likesService;

  @GetMapping("/")
  public String index(@AuthenticationPrincipal CustomOAuth2User user , Model model, @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) Integer minPrice, @RequestParam(required = false) String category,@PageableDefault(size = 5) Pageable pageable) {

    Member currentUser = (user != null) ? user.getMember() : null;

    // 게시글 목록을 가져오는 서비스 메서드 호출
    Page<ItemPostResponse> posts = postService.getAllPosts(category, minPrice, maxPrice, currentUser, pageable);
    model.addAttribute("posts",posts);
    model.addAttribute("categories", Category.values());
    return "index";
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
