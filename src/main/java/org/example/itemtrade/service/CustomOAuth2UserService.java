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
@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId(); // kakao, google

    String providerId;

    String email = null;

    if (provider.equals("kakao")){
      providerId = oAuth2User.getAttribute("id").toString();
      Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");// kakao oauth2는 kakao_account에 정보가 담겨있음 그래서 account를 받아와서 정보를 가져와야 함
      email = ((String) kakaoAccount.get("email")).trim().toLowerCase();

    }else if(provider.equals("google")) {
      providerId = oAuth2User.getAttribute("sub");
      email = ((String) oAuth2User.getAttribute("email")).trim().toLowerCase();
    }else{
      throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
    }

    // 회원 조회 또는 생성
    Member member = memberRepository
        .findByProviderAndProviderId(provider, providerId)  // ✅ 이걸로 식별해야
        .orElseGet(() -> {
          Member newMember = MemberOauth2.create(userRequest, oAuth2User);
          return memberRepository.save(newMember);
        });
    if (member.isDeleted()) {
      throw new DisabledException("탈퇴한 회원입니다.");
    }
    if (providerId == null || providerId.isBlank()) {
      throw new OAuth2AuthenticationException("소셜 로그인에서 사용자 식별이 불가능합니다.");
    }
    return new CustomOAuth2User(member, oAuth2User.getAttributes());
  }

}
