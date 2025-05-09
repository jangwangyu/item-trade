package org.example.itemtrade.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberBlock {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Member blocker; // 차단한 회원

  @ManyToOne
  private Member blocked; // 차단당한 회원

  private LocalDateTime createdAt; // 차단한 날짜

  public static MemberBlock of(Member blocker, Member blocked) {
    return new MemberBlock(blocker, blocked);
  }

  private MemberBlock(Member blocker, Member blocked) {
    this.blocker = blocker;
    this.blocked = blocked;
    this.createdAt = LocalDateTime.now();
  }

}
