package org.example.itemtrade.dto;

import org.example.itemtrade.domain.Member;

public record MemberDto(
    Long id,
    String email,
    String password,
    String name,
    String nickName,
    String profileImageUrl,
    String phoneNumber,
    String role) {
  public static MemberDto from(Member member) {
      return new MemberDto(member.getId(),member.getEmail(),member.getPassword(), member.getName(),member.getNickName(),member.getProfileImageUrl(), member.getPhoneNumber(), member.getRole());
  }

  public static Member toEntity(MemberDto memberDto) {
    return Member.builder()
        .id(memberDto.id)
        .email(memberDto.email())
        .password(memberDto.password())
        .name(memberDto.name())
        .nickName(memberDto.nickName())
        .profileImageUrl(memberDto.profileImageUrl())
        .phoneNumber(memberDto.phoneNumber())
        .role(memberDto.role())
        .build();
  }
}
