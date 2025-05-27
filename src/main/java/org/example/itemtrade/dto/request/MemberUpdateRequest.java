package org.example.itemtrade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class MemberUpdateRequest {
  private String nickName;

  private String profileImageUrl;

  // 자기소개
  private String introduction;

}
