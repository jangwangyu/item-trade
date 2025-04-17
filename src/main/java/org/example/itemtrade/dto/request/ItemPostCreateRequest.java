package org.example.itemtrade.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;

@Getter
@Setter
@NoArgsConstructor
public class ItemPostCreateRequest {
  private String title;
  private String description;
  private int price;
  private String category;
  private String imagePath;

  public ItemPostCreateRequest(String title, String description, int price, String category) {
    this.title = title;
    this.description = description;
    this.price = price;
    this.category = category;
  }

  public ItemPost of(Member seller) {
    return ItemPost.builder()
        .title(title)
        .description(description)
        .price(price)
        .category(category)
        .seller(seller)
        .imagePath(imagePath)
        .isSold(false)
        .build();
  }

}

