package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@RequiredArgsConstructor
@Controller
public class mainController {

  private final PostService postService;

  @GetMapping("/")
  public String index(Model model) {
    var posts = postService.getAllPosts();
    model.addAttribute("posts",posts);
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
