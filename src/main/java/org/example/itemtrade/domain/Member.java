package org.example.itemtrade.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Member {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  @Id
  private Long id;

  // 소셜 로그인 시 사용되는 정보
  private String provider;
  private String providerId;

  @Column(nullable = false)
  private String email;

  private String password;

  @Column(nullable = true)
  private String name;
  @Column(nullable = true)
  private String nickName;
  @Column(nullable = true)
  private String phoneNumber;
  @Column(nullable = true)
  private String profileImageUrl;
  @Column(nullable = true)
  private LocalDate birthDay;

  private boolean deleted = false;

  private String role;

  @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemPost> itemPosts = new ArrayList<>();

  public Member(String email, String nickName) {
    this.email = email;
    this.nickName = nickName;
  }

  // 연관관계 편의 메서드
  public void addItemPost(ItemPost post) {
    itemPosts.add(post);
    post.setSeller(this);
  }

  public void removeItemPost(ItemPost post) {
    itemPosts.remove(post);
    post.setSeller(null);
  }

  @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  public void addComment(Comment comment) {
    comments.add(comment);
    comment.setWriter(this);
  }

  @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatRoom> buyRooms = new ArrayList<>();

  @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatRoom> sellRooms = new ArrayList<>();


  public void addBuyRoom(ChatRoom room) {
    buyRooms.add(room);
    room.setBuyer(this);
  }

  public void addSellRoom(ChatRoom room) {
    sellRooms.add(room);
    room.setSeller(this);
  }

  // 회원 탈퇴
  public void delete() {
    this.deleted = true;
  }

  public void restore() {
    this.deleted = false;
  }

  public boolean isDeleted() {
    return this.deleted;
  }


  public Member getMember() {
    return this;
  }
}
