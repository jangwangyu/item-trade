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
import org.example.itemtrade.dto.SoftDelete;

@Getter
@Entity
public class ItemImage implements SoftDelete {
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
