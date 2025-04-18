package org.example.itemtrade.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.enums.Category;

@Setter
@Getter
@NoArgsConstructor
public class ItemPostUpdateRequest {
    private String title;
    private String description;
    private int price;
    private Category category;

  public ItemPostUpdateRequest(String title, String description, int price, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
  }


  public void of(ItemPost post) {
        post.setTitle(this.title);
        post.setDescription(this.description);
        post.setPrice(this.price);
        post.setCategory(this.category);
    }
}