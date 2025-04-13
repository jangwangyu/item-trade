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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Member {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  @Id
  private Long id;

  @Column(nullable = false)
  private String email;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String nickName;
  @Column(nullable = false)
  private String phoneNumber;
  private String profileImageUrl;
  @Column(nullable = false)
  private LocalDate birthDay;

  private boolean deleted = false;

  private String role;

  @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemPost> itemPosts = new ArrayList<>();

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

  public void delete() {
    this.deleted = true;
  }

  public void restore() {
    this.deleted = false;
  }

  public boolean isDeleted() {
    return this.deleted;
  }



}
