package org.example.itemtrade.contoller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.request.MemberJoinRequest;
import org.example.itemtrade.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class JoinController {
  private final MemberService memberService;

//  // 회원가입 페이지로 이동
//  @GetMapping("/api/register")
//  public String register(Model model) {
//    model.addAttribute("registerRequest", new MemberJoinRequest());
//    return "register";
//  }

  @ResponseBody
  @PostMapping("/api/register")
  public ResponseEntity<?> registerPost(@RequestBody MemberJoinRequest request) {
    try{
      memberService.join(request);
      return ResponseEntity.ok().build();
    }catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

}
