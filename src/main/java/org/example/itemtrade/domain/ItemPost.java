package org.example.itemtrade.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.dto.SoftDelete;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.enums.TradeStatus;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ItemPost implements SoftDelete { // 판매글
  @Id
  @Column(name = "item_post_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private int price;

  @Enumerated(EnumType.STRING)
  private Category category; // ex. 카테고리
  private boolean isSold;
  private String imageUrl; // 이미지 URL

  @Enumerated(EnumType.STRING)
  private TradeStatus status = TradeStatus.TRADE;// 뷰 노출용

  @Column(nullable = false)
  private int likeCount = 0; // 좋아요 수



  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id")
  private Member seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private Member buyer;

  @Builder.Default
  @OneToMany(mappedBy = "itemPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemImage> images = new ArrayList<>();

  public void addImage(ItemImage image) {
    images.add(image);
    image.setItemPost(this); // 양방향 연관관계 유지
  }

  @OneToMany(mappedBy = "itemPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  public void addComment(Comment comment) {
    comments.add(comment);
    comment.setItemPost(this);
  }

  @OneToMany(mappedBy = "itemPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatRoom> chatRooms = new ArrayList<>();

  @OneToMany
  (mappedBy = "itemPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Like> likes = new ArrayList<>();

  public void addChatRoom(ChatRoom chatRoom) {
    chatRooms.add(chatRoom);
    chatRoom.setItemPost(this);
  }

  // 생성 시 자동 시간 기록
  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  public void update(ItemPostUpdateRequest request) {
    this.title = request.getTitle();
    this.description = request.getDescription();
    this.price = request.getPrice();
    this.category = request.getCategory();
  }

  private boolean deleted = false;

  @Override
  public void softDelete() {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted() {
    return this.deleted;
  }


}
