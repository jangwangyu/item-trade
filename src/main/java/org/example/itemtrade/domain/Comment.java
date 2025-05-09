package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.dto.SoftDelete;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Comment implements SoftDelete { // 댓글
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  @Id
  private Long id;

  private String content;
  private LocalDateTime createdAt;

  private boolean deleted = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "writer_id")
  private Member writer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }

  public static Comment of(Member writer, ItemPost itemPost, String content) {
    Comment comment = new Comment();
    comment.writer = writer;
    comment.itemPost = itemPost;
    comment.content = content;
    return comment;
  }


  @Override
  public void softDelete() {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted() {
    return this.deleted;
  }
}
