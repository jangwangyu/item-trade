package org.example.itemtrade.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
    String email = ((String) kakaoAccount.get("email")).trim().toLowerCase();
    Member member = memberRepository.findByEmail(email).orElseGet(() -> {
      Member newMember = MemberOauth2.create(userRequest, oAuth2User);
      return memberRepository.save(newMember);
    });
    System.out.println("email=[" + email + "]");
    if (member.isDeleted()) {
      throw new DisabledException("탈퇴한 회원입니다.");
    }
    return new CustomOAuth2User(member, oAuth2User.getAttributes());
  }

  public void deleteMember(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    member.delete();
  }

  @Transactional
  public void restoreMember(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    if (!member.isDeleted()) {
      throw new IllegalArgumentException("이미 활성화 된 회원입니다.");
    }

    member.restore();
  }




}
