package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class mainController {

  private final PostService postService;

  @GetMapping("/")
  public String index(@AuthenticationPrincipal CustomOAuth2User user , Model model, @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) Integer minPrice, @RequestParam(required = false) String category) {

    Member currentUser = (user != null) ? user.getMember() : null;

    // 게시글 목록을 가져오는 서비스 메서드 호출
    List<ItemPostResponse> posts = postService.getAllPosts(category, minPrice, maxPrice, currentUser);
    model.addAttribute("posts",posts);
    model.addAttribute("categories", Category.values());
    return "index";
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
