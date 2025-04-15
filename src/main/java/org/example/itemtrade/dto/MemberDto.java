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

  public static Member toEntity(MemberDto memberDto) {
    return Member.builder()
        .id(memberDto.id)
        .email(memberDto.email())
        .nickName(memberDto.nickName())
        .profileImageUrl(memberDto.profileImageUrl())
        .birthDay(memberDto.birthDay())
        .role(memberDto.role())
        .build();
  }
}
