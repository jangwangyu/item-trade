package org.example.itemtrade.contoller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostContoller {

  private final PostService postService;

  // 게시글 조회
  @GetMapping("/{id}")
  public String showDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomOAuth2User user) {

    var post = postService.getPostById(id);

    boolean isAuthor = user != null && post.sellerId().equals(user.getMember().getId());

    model.addAttribute("post", post);
    model.addAttribute("isAuthor", isAuthor);

    return "/detail";
  }

  // 게시글 작성
  @GetMapping("/new")
  public String createPostView(Model model) {
    model.addAttribute("post", new ItemPostCreateRequest());
    model.addAttribute("categories", Category.values());
    return "post-form";
  }

  @PostMapping
  public String createPost(@ModelAttribute ItemPostCreateRequest request, @AuthenticationPrincipal CustomOAuth2User user, @RequestParam("imageUrl") MultipartFile image)
      throws IOException {

    postService.createPost(request, user, image);

    return "redirect:/";
  }

  // 수정 view
  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    ItemPostResponse post = postService.getPostForEdit(id);
    model.addAttribute("post", post);
    model.addAttribute("categories", Category.values());
    return "/update-form";
  }

  // 게시글 수정
  @PutMapping("/edit/{id}")
  public String updatePost(@PathVariable Long id,
      @ModelAttribute ItemPostUpdateRequest request,
      @AuthenticationPrincipal CustomOAuth2User user,
      @RequestParam(value = "imageUrl", required = false) MultipartFile image) throws IOException {

    postService.updatePost(id, request, user.getMember(), image);

    return "redirect:/posts/" + id;
  }

  // 게시글 삭제
  @DeleteMapping("/{id}/delete")
  public String deletePost(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user) {

    postService.deletePost(id, user.getMember());

    return "redirect:/";
  }
}
