package org.example.itemtrade.contoller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.service.CommentService;
import org.example.itemtrade.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class PostController {

  private final PostService postService;

  private final CommentService commentService;

  // 게시글 조회
  @GetMapping("/{id}")
  public String showDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal(expression = "member") Member member, Pageable pageable) {

    var post = postService.getPostById(id);

    boolean isAuthor = member != null && post.sellerId().equals(member.getId());

    // 댓글 리스트
    Page<CommentDto> comments = commentService.commentList(id,pageable);

    model.addAttribute("post", post);
    model.addAttribute("isAuthor", isAuthor);
    model.addAttribute("comments", comments);
    if (member != null && member.getId() != null) {
      model.addAttribute("currentUserId", member.getId());
    } else {
      model.addAttribute("currentUserId", null);
    }
    return "detail";
  }

  // 게시글 작성
  @GetMapping("/new")
  public String createPostView(Model model) {
    model.addAttribute("post", new ItemPostCreateRequest());
    model.addAttribute("categories", Category.values());
    return "post-form";
  }

  @PostMapping
  public String createPost(@ModelAttribute ItemPostCreateRequest request, @AuthenticationPrincipal(expression = "member") Member member, @RequestParam("images") List<MultipartFile> images)
      throws IOException {

    postService.createPost(request, member, images);

    return "redirect:/";
  }

  // 수정 view
  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    ItemPostResponse post = postService.getPostForEdit(id);
    model.addAttribute("post", post);
    model.addAttribute("categories", Category.values());
    return "update-form";
  }

  // 게시글 수정
  @PutMapping("/edit/{id}")
  public String updatePost(@PathVariable Long id,
      @ModelAttribute ItemPostUpdateRequest request,
      @AuthenticationPrincipal(expression = "member") Member member,
      @RequestParam(value = "images", required = false) List<MultipartFile> images,
      @RequestParam(value = "deletedItemImageIds", required = false) List<Long> deletedItemImageIds) throws IOException {

    postService.updatePost(id, request, member, images, deletedItemImageIds);

    return "redirect:/posts/" + id;
  }

  // 게시글 삭제
  @DeleteMapping("/{id}/delete")
  public String deletePost(@PathVariable Long id, @AuthenticationPrincipal(expression = "member") Member member) {

    postService.deletePost(id, member.getMember());

    return "redirect:/";
  }


}
