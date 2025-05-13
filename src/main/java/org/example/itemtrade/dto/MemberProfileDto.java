package org.example.itemtrade.dto;

import java.time.LocalDateTime;
import org.example.itemtrade.domain.Member;

public record MemberProfileDto (
    Long id,
    String nickName,
    String profileImageUrl,
    String introduction,
    LocalDateTime createdAt,
    int totalTrades
){
  public static MemberProfileDto from(Member member, int totalTrades) {
    return new MemberProfileDto(
        member.getId(),
        member.getNickName(),
        member.getProfileImageUrl(),
        member.getIntroduction(),
        member.getCreatedAt(),
        totalTrades
    );
  }

}
