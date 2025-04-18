package org.example.itemtrade.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.enums.Category;

@Getter
@Setter
@NoArgsConstructor
public class ItemPostCreateRequest {
  private String title;
  private String description;
  private int price;
  private Category category;
  private String imagePath;

  public ItemPostCreateRequest(String title, String description, int price, Category category) {
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

