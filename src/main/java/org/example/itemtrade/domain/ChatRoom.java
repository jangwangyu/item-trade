package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.dto.SoftDelete;
import org.example.itemtrade.enums.TradeStatus;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Entity
public class ChatRoom implements SoftDelete {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  @Id
  private Long id;

  private String title;

  private LocalDateTime createAt;

  private LocalDateTime lastMessageCreatedAt;

  private boolean deleted = false;

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

  @Override
  public void softDelete() {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted() {
    return this.deleted;
  }

  public Member getOpponent(Member me) {
    if (this.buyer.equals(me)) {
      return this.seller;
    } else if (this.seller.equals(me)) {
      return this.buyer;
    }
    throw new IllegalArgumentException("해당 사용자는 채팅방의 멤버가 아닙니다.");
  }
}
