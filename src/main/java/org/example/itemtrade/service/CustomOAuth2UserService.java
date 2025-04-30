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

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");// kakao oauth2는 kakao_account에 정보가 담겨있음 그래서 account를 받아와서 정보를 가져와야 함
    String email = ((String) kakaoAccount.get("email")).trim().toLowerCase();
    Member member = memberRepository.findByEmail(email).orElseGet(() -> {
      Member newMember = MemberOauth2.create(userRequest, oAuth2User);
      return memberRepository.save(newMember);
    });
    if (member.isDeleted()) {
      throw new DisabledException("탈퇴한 회원입니다.");
    }
    return new CustomOAuth2User(member, oAuth2User.getAttributes());
  }

}
