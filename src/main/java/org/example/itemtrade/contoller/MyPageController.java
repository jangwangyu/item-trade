package org.example.itemtrade.contoller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.User.UserType;
import org.example.itemtrade.dto.request.MemberUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.service.MemberService;
import org.example.itemtrade.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RequiredArgsConstructor
@Controller
public class MyPageController {
  private final MyPageService myPageService;
  private final MemberService memberService;

  // 마이페이지
  @GetMapping("/mypage")
  public String myPage(@AuthenticationPrincipal UserType user, Model model) {
    Member member = memberService.getMember(user.getMember());
    String loginType = user.getLoginType();

    List<ItemPostResponse> posts = myPageService.getMyPosts(member);
    List<Member> blockedMembers = memberService.getBlockedMembers(member);

    model.addAttribute("posts", posts);
    model.addAttribute("loginType", loginType);
    model.addAttribute("member", member);
    model.addAttribute("blockedMembers", blockedMembers);

    return "/mypage";
  }

  // 회원정보 수정
  @PutMapping("/mypage/update")
  public String updateMember(@AuthenticationPrincipal UserType user, MemberUpdateRequest memberUpdateRequest) {
    Member member = user.getMember();
    memberService.updateMember(member, memberUpdateRequest, user);
    return "redirect:/mypage";
  }

  // 차단해제
  @PostMapping("/unblock/{blockedId}")
  public String unblockMember(@AuthenticationPrincipal UserType user, @PathVariable Long blockedId) {
    Member member = user.getMember();
    Member blockedMember = memberService.getMemberId(blockedId);
    memberService.unBlockMember(member, blockedMember);
    return "redirect:/mypage";
  }

  // 회원 탈퇴
  @PostMapping("/mypage/delete")
  public String deleteMember(@AuthenticationPrincipal UserType user) {
    Member member = user.getMember();
    myPageService.deleteMember(member);
    return "redirect:/logout";
  }
}
