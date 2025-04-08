package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

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
}
