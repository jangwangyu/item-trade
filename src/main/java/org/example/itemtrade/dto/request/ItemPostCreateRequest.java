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

