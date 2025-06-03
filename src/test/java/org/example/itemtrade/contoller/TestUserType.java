package org.example.itemtrade.contoller;

import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.User.UserType;

public class TestUserType implements UserType {
  private final Member member;
  private final String loginType;

  public TestUserType(Member member, String loginType) {
    this.member = member;
    this.loginType = loginType;
  }

  @Override
  public Member getMember() {
    return member;
  }

  @Override
  public String getLoginType() {
    return loginType;
  }
}
