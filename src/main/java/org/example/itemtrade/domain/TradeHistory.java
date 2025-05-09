package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class TradeHistory { // 거래 완료 내역

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trade_history_id")
  @Id
  private Long id;

  @ManyToOne
  private Member buyer;

  @ManyToOne
  private Member seller;

  @ManyToOne
  private ItemPost itemPost;

  private LocalDateTime createAt;

  public static TradeHistory of(Member buyer, Member seller, ItemPost itemPost) {
    TradeHistory trade = new TradeHistory();
    trade.buyer = buyer;
    trade.seller = seller;
    trade.itemPost = itemPost;
    trade.createAt = LocalDateTime.now();
    return trade;
  }
}
