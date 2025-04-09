package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Setter;

@Setter
@Entity
public class ChatRoom {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  @Id
  private Long id;

  String title;

  LocalDateTime createAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private Member buyer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private Member seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  public void setBuyer(Member buyer) {
    this.buyer = buyer;
  }

  public void setSeller(Member seller) {
    this.seller = seller;
  }

  public void setItemPost(ItemPost itemPost) {
    this.itemPost = itemPost;
  }

}
