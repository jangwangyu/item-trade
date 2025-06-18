package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.dto.SoftDelete;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ItemImage implements SoftDelete {
  @Id
  @Column(name = "item_image_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imagePath;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_post_id")
  private ItemPost itemPost;

  public static ItemImage of(String imagePath) {
    ItemImage image = new ItemImage();
    image.imagePath = imagePath;
    return image;
  }

  public ItemImage(String imagePath, ItemPost itemPost) {
    this.imagePath = imagePath;
    this.itemPost = itemPost;
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
