package org.example.itemtrade.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.enums.Role;

@Getter
@Setter
public class MemberJoinRequest {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 아닙니다.")
  private String email;
  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;
  @NotBlank(message = "비밀번호 확인은 필수입니다.")
  private String confirmPassword;
  @NotBlank(message = "닉네임은 필수입니다.")
  private String nickname;

  public Member toEntity(String encodedPassword) {
    return Member.builder()
        .email(email)
        .password(encodedPassword)
        .nickName(nickname)
        .role(Role.USER.getCode())
        .build();
  }

}
