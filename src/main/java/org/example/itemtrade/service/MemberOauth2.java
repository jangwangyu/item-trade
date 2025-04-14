package org.example.itemtrade.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.enums.Role;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class MemberOauth2 {
  public static Member create(OAuth2UserRequest request, OAuth2User oauth2User) {
    return switch (request.getClientRegistration().getRegistrationId()) {
      case "kakao" -> {
        Map<String, Object> attributes = oauth2User.getAttribute("kakao_account");
        yield Member.builder()
            .email(attributes.get("email").toString())
            .nickName(((Map) attributes.get("profile")).get("nickname").toString())
            .birthDay(getBirthDay(attributes))
            .role(Role.USER.getCode())
            .build();
      }
      case "google" -> {
        Map<String, Object> attributes = oauth2User.getAttributes();
        yield Member.builder()
            .email(attributes.get("email").toString())
            .name(attributes.get("name").toString())
            .nickName(attributes.get("given_name").toString())
            .role(Role.USER.getCode())
            .build();
      }
      default -> throw new IllegalArgumentException("연동되지 않는 서비스입니다.");
    };
  }

  private static LocalDate getBirthDay(Map<String, Object> attributes) {
    String birthYear = (String) attributes.get("birthyear");
    String birthDay = (String) attributes.get("birthday");
    return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
  }

}
