package org.example.itemtrade.dto;

import java.time.LocalDate;
import org.example.itemtrade.domain.Member;

public record MemberDto(
    Long id,
    String email,
    String password,
    String nickName,
    String profileImageUrl,
    LocalDate birthDay,
    String role) {

  public static MemberDto from(Member member) {
      return new MemberDto(
          member.getId(),
          member.getEmail(),
          null,
          member.getNickName(),
          member.getProfileImageUrl(),
          member.getBirthDay(),
          member.getRole());
  }

}
