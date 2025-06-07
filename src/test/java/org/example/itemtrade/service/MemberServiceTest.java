package org.example.itemtrade.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.example.itemtrade.dto.MemberDto;
import org.example.itemtrade.dto.MemberProfileDto;
import org.example.itemtrade.dto.User.CustomOAuth2User;
import org.example.itemtrade.dto.User.UserType;
import org.example.itemtrade.dto.request.MemberJoinRequest;
import org.example.itemtrade.dto.request.MemberUpdateRequest;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private ItemPostRepository itemPostRepository;

  @Mock
  private MemberBlockRepository memberBlockRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberService memberService;

  @Test
  void 회원가입_정상(){
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "password", "password", "테스트");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(memberRepository.existsByNickName("테스트")).thenReturn(false);
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    when(memberRepository.save(any(Member.class))).thenReturn(
        Member.builder().id(1L).email("test@test.com").nickName("테스트")
            .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg").build());
    // When
    memberService.join(request);
    // Then
    verify(memberRepository, times(1)).save(any(Member.class));
  }

  @Test
  void 회원가입_이미_존재하는_이메일() {
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "password", "password",
        "테스트");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 가입된 이메일입니다.");
  }
  @Test
  void 회원가입_비밀번호_불일치(){
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "password", "asd",
        "테스트");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
    // When & Then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("비밀번호가 일치하지 않습니다.");
  }
  @Test
  void 회원가입_이미_존재하는_닉네임(){
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "password", "password",
        "테스트");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(memberRepository.existsByNickName("테스트")).thenReturn(true);
    //When & Then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 가입된 닉네임입니다.");
  }

  @Test
  void 회원가입_닉네임_길이_제한() {
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "password", "password",
        "테");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(memberRepository.existsByNickName("테")).thenReturn(false);
    // When & Then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("닉네임은 2자 이상 10자 이하로 입력해주세요.");
  }

  @Test
  void 회원가입_비밀번호_길이_제한() {
    // Given
    MemberJoinRequest request = new MemberJoinRequest("test@test.com", "asd", "asd",
        "테스트");
    when(memberRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(memberRepository.existsByNickName("테스트")).thenReturn(false);
    // When & Then
    assertThatThrownBy(() -> memberService.join(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("비밀번호는 8자 이상 20자 이하로 입력해주세요.");
  }

  @Test
  void 회원정보_수정() {
    // Given
    Member member = Member.builder()
        .email("test@test.com")
        .nickName("테스트")
        .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg")
        .introduction("안녕하세요")
        .build();
    MemberUpdateRequest request = new MemberUpdateRequest( "123","https://api.dicebear.com/7.x/adventurer/svg?seed=1234567.svg", "반갑습니다.");
    UserType userType = mock(UserType.class);

    when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
    when(userType.getLoginType()).thenReturn("FORM");

    // When
    memberService.updateMember(member, request, userType);
    // Then
    verify(memberRepository, times(1)).save(any(Member.class));
    assertThat(member.getNickName()).isEqualTo("123");
    assertThat(member.getIntroduction()).isEqualTo("반갑습니다.");
  }

  @Test
  void 상대방_프로필_조회() {
    // Given
    Member member = Member.builder()
        .id(1L)
        .email("test@test.com")
        .nickName("테스트")
        .profileImageUrl("https://api.dicebear.com/7.x/adventurer/svg?seed=12345.svg")
        .introduction("안녕하세요")
        .build();
    when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    when(itemPostRepository.countBySellerAndStatus(member, TradeStatus.COMPLETE)).thenReturn(3);
    when(itemPostRepository.countByBuyerAndStatus(member, TradeStatus.COMPLETE)).thenReturn(1);
    // When
    MemberProfileDto result = memberService.getMemberProfile(member.getId());
    // Then
    assertThat(result.nickName()).isEqualTo("테스트");
    assertThat(result.totalTrades()).isEqualTo(4);
    verify(memberRepository, times(1)).findById(1L);
  }



  @Test
  void 유저를_차단한다() {
    // given
    Member blocker = Member.builder().id(1L).email("test@test.com").build();
    Member blocked = Member.builder().id(2L).email("test2@test.com").build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(blocker));
    when(memberRepository.findById(2L)).thenReturn(Optional.of(blocked));
    when(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)).thenReturn(false);

    // when
    memberService.blockMember(blocker, blocked);

    // then
    verify(memberBlockRepository, times(1)).save(any(MemberBlock.class));

  }

  @Test
  void 이미_차단한_유저는_예외를_던진다() {
    // given
    Member blocker = Member.builder().id(1L).email("test@test.com").build();
    Member blocked = Member.builder().id(2L).email("test2@test.com").build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(blocker));
    when(memberRepository.findById(2L)).thenReturn(Optional.of(blocked));
    when(memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked)).thenReturn(true);


    // when & then
    assertThatThrownBy(() -> memberService.blockMember(blocker, blocked))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 차단된 회원입니다.");

  }

  @Test
  void 유저를_차단_해제한다() {
    // Given
    Member blocker = Member.builder().id(1L).email("test.com").build();
    Member blocked = Member.builder().id(2L).email("test.com").build();
    MemberBlock block = MemberBlock.of(blocker,blocked);

    when(memberRepository.findById(1L)).thenReturn(Optional.of(blocker));
    when(memberRepository.findById(2L)).thenReturn(Optional.of(blocked));
    when(memberBlockRepository.findByBlockerAndBlocked(blocker, blocked)).thenReturn(Optional.of(block));
    // When
    memberService.unBlockMember(blocker, blocked);
    // Then
    verify(memberBlockRepository).delete(block);
  }

  @Test
  void 차단된_유저를_차단_해제할때_예외() {
    // Given
    Member blocker = Member.builder().id(1L).email("test.com").build();
    Member blocked = Member.builder().id(2L).email("test.com").build();

    when(memberRepository.findById(1L)).thenReturn(Optional.of(blocker));
    when(memberRepository.findById(2L)).thenReturn(Optional.of(blocked));
    when(memberBlockRepository.findByBlockerAndBlocked(blocker, blocked)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> memberService.unBlockMember(blocker, blocked))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("차단된 회원이 아닙니다.");
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


