package org.example.itemtrade.dto.request;

import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;

public class ItemPostCreateRequest {
  private String title;
  private String description;
  private int price;
  private String category;

  public ItemPost toEntity(Member seller) {
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

