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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class ChatRoom {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  @Id
  private Long id;

  private String title;

  private LocalDateTime createAt;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private Member buyer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private Member seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  public static ChatRoom of(Member buyer, Member seller, ItemPost itemPost) {
    ChatRoom chatRoom = new ChatRoom();
    chatRoom.buyer = buyer;
    chatRoom.seller = seller;
    chatRoom.itemPost = itemPost;
    return chatRoom;
  }

  private boolean deletedByBuyer = false;
  private boolean deletedBySeller = false;

  public void deleted(Member member){
    if(this.buyer.getId().equals(member.getId())) {
      this.deletedByBuyer = true;
    } else if(this.seller.getId().equals(member.getId())) {
      this.deletedBySeller = true;
    } else {
      throw new IllegalArgumentException("권한이 없습니다.");
    }
  }

  public boolean isVisible(Member member) {
    if(this.buyer.equals(member)) return !deletedByBuyer;
    if(this.seller.equals(member)) return !deletedBySeller;
    return false;
  }

  public void restoreFor(Member member){
    if(this.buyer != null && buyer.equals(member)) {
      this.deletedBySeller = false;
    }else if (this.seller != null && seller.equals(member)) {
      this.deletedByBuyer = false;
    }
  }

}
