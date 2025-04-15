package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberService memberService;

  @Test
  void 유저를_비활성화한다() {
    Member member = new Member();
    member.setEmail("test@example.com");

    when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

    memberService.deleteMember("test@example.com");

    assertThat(member.isDeleted()).isTrue();
  }

  @Test
  void 유저를_비활성화를_해제한다() {
    Member member = new Member();
    member.setEmail("test@example.com");
    member.delete(); // deleted = true

    when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

    memberService.restoreMember("test@example.com");

    assertThat(member.isDeleted()).isFalse();
  }

  @Test
  void 비활성화된유저를다시활성화_예외() {
    Member member = new Member();
    member.setEmail("test@example.com"); // deleted = false

    when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

    assertThatThrownBy(() -> memberService.restoreMember("test@example.com"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 활성화");
  }

  @Test
  void 없는회원을비활성화하려고하는_예외() {
    when(memberRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> memberService.deleteMember("none@example.com"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("회원이 아닙니다");
  }

  @Test
  void 카카오_로그인_정상작동() {
    // 1. 카카오 계정 정보 mock 데이터 구성
    // given()
    Map<String, Object> kakaoProfile = Map.of("nickname", "완구");
    Map<String, Object> kakaoAccount = Map.of(
        "email", "test@kakao.com",
        "profile", kakaoProfile
    );
    Map<String, Object> attributes = Map.of(
        "id", "1234567890",
        "kakao_account", kakaoAccount
    );

    // 2. 테스트용 Member 객체 생성 (DB에 있다고 가정)
    Member mockMember = new Member("test@kakao.com", "완구");

    // 3. 가짜 userRequest 생성 (클라이언트, 토큰 등 포함)
    OAuth2UserRequest userRequest = createMockUserRequest();

    // 4. 가짜 DefaultOAuth2User 객체 생성 (Spring 내부의 사용자 정보 객체)
    OAuth2User mockOAuth2User = new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_USER")), // 권한
        attributes,  // OAuth2 로그인 응답 데이터
        "id"         // 사용자 이름으로 쓸 키
    );

    // 5. CustomOAuth2UserService를 익명 클래스로 오버라이딩해서
    // super.loadUser(userRequest) 대신 직접 CustomOAuth2User 리턴하도록 설정
    CustomOAuth2UserService testService = new CustomOAuth2UserService(memberRepository) {
      @Override
      public OAuth2User loadUser(OAuth2UserRequest request) {
        return new CustomOAuth2User(mockMember, attributes);
      }
    };

    // 6. 실제 테스트 실행: loadUser 호출
    OAuth2User result = testService.loadUser(userRequest);

    // 7. 결과가 CustomOAuth2User 타입인지 확인하고, 이메일/닉네임이 일치하는지 검증
    assertTrue(result instanceof CustomOAuth2User);
    CustomOAuth2User customUser = (CustomOAuth2User) result;
    assertEquals("test@kakao.com", customUser.getMember().getEmail());
    assertEquals("완구", customUser.getMember().getNickName());
  }

// OAuth2UserRequest를 직접 생성하는 헬퍼 메서드
// 실제 카카오 인증 서버로 요청 보내지 않기 위해 필요한 mock 구성
  private OAuth2UserRequest createMockUserRequest() {
    // 클라이언트 등록 정보 (clientId, redirectUri 등)
    ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao")
        .clientId("client-id")
        .clientSecret("client-secret")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/kakao")
        .authorizationUri("https://kauth.kakao.com/oauth/authorize")
        .tokenUri("https://kauth.kakao.com/oauth/token")
        .userInfoUri("https://kapi.kakao.com/v2/user/me")
        .userNameAttributeName("id")  // 사용자 고유 식별자
        .clientName("Kakao")
        .build();

    // 토큰 생성 (가짜 access token)
    OAuth2AccessToken accessToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "mock-token", // 실제 카카오 토큰 아님
        Instant.now(),
        Instant.now().plusSeconds(3600) // 1시간 유효
    );

    // 최종 OAuth2UserRequest 객체 반환
    return new OAuth2UserRequest(clientRegistration, accessToken);
  }


}


