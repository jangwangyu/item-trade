package org.example.itemtrade.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.request.MemberLoginRequest;
import org.example.itemtrade.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

  private final MemberService memberService;

  @GetMapping("/login-form")
  public String login() {
    return "login-form";
  }

  @PostMapping("/login")
  public String loginPost(@Valid MemberLoginRequest request, BindingResult result) {
    if (result.hasErrors()) {
      return "login-form"; // 에러가 발생하면 로그인 페이지로 다시 이동
    }
    memberService.login(request);
    return "redirect:/"; // 로그인 후 메인 페이지로 리다이렉트
  }

}
