package org.example.itemtrade.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.request.MemberJoinRequest;
import org.example.itemtrade.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class JoinController {
  private final MemberService memberService;
  // 회원가입 페이지로 이동
  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("registerRequest", new MemberJoinRequest());
    return "/register";
  }

  @PostMapping("/register")
  public String registerPost(@Valid MemberJoinRequest request, BindingResult result) {
    if (result.hasErrors()) {
      return "/register"; // 에러가 발생하면 회원가입 페이지로 다시 이동
    }
    memberService.join(request);
    return "redirect:/login-form"; // 회원가입 후 로그인 페이지로 리다이렉트
  }

}
