package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.request.LoginRequest;
import org.example.itemtrade.dto.response.JwtResponse;
import org.example.itemtrade.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {
  private final MemberService memberService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    JwtResponse response = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());
    return ResponseEntity.ok(response);
  }

}
