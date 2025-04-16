package org.example.itemtrade.dto.request;

import lombok.Getter;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;

@Getter
public class ItemPostCreateRequest {
  private String title;
  private String description;
  private int price;
  private String category;

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
        .isSold(false)
        .build();
  }
}

