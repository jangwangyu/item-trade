package org.example.itemtrade.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
  String email;
  @Column(nullable = false)
  String password;
  @Column(nullable = false)
  String name;
  @Column(nullable = false)
  String nickName;
  @Column(nullable = false)
  String phoneNumber;
  @Column(nullable = false)
  String profileImageUrl;

  String role;

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

}
