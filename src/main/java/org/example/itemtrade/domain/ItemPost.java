package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ItemPost { // 판매글
  @Id
  @Column(name = "item_post_id")
  @GeneratedValue
  private Long id;

  private String title;
  private String description;
  private int price;

  private String category; // ex. 게임 이름
  private boolean isSold;

  @ManyToOne
  private Member seller;

  private LocalDateTime createdAt;
}
