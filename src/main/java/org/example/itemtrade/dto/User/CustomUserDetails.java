package org.example.itemtrade.dto.User;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.itemtrade.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails, UserType {

  @Getter
  private final Member member;

  @Override
  public String getLoginType() {
    return "FORM";
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(this.member::getRole);
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }
}
