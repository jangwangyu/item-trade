package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ItemImage {
  @Id
  @Column(name = "item_image_id")
  @GeneratedValue
  private Long id;

  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  public void setItemPost(ItemPost itemPost) {
    this.itemPost = itemPost;
  }
}
