package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.MemberProfileDto;
import org.example.itemtrade.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class MemberController {

  private final MemberService memberService;

  @GetMapping("/opponent/{memberId}")
  public String getMemberProfile(@PathVariable(value = "memberId") Long memberId, Model model) {

    // 회원 프로필 조회 로직
    MemberProfileDto profile = memberService.getMemberProfile(memberId);

    model.addAttribute("profile", profile);

    return "opponentpage"; // 프로필 페이지로 이동
  }
}
