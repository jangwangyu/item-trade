package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class ItemImage {
  @Id
  @Column(name = "item_image_id")
  @GeneratedValue
  private Long id;

  private String imageUrl;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  public static ItemImage of(String imageUrl, ItemPost itemPost) {
    ItemImage image = new ItemImage();
    image.imageUrl = imageUrl;
    image.itemPost = itemPost;
    return image;
  }

}
