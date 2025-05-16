package org.example.itemtrade.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.enums.Category;

@Getter
@Setter
@NoArgsConstructor
public class ItemPostCreateRequest {
  private String title;
  private String description;
  private int price;
  private Category category;

  public ItemPostCreateRequest(String title, String description, int price, Category category) {
    this.title = title;
    this.description = description;
    this.price = price;
    this.category = category;
  }

}

