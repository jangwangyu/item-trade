package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

  private final MemberService memberService;

  @GetMapping("/login-form")
  public String login() {
    return "/login-form";
  }

}
