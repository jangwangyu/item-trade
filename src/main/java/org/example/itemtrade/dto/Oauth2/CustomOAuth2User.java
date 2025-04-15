package org.example.itemtrade.dto.Oauth2;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.itemtrade.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User { // Member 객체와 OAuth2 로그인 정보를 연결해주는 용도

  @Getter
  private final Member member;


  private final Map<String, Object> attributes;

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> this.member.getRole());
  }

  @Override
  public String getName() {
    return this.member.getNickName();
  }
}
