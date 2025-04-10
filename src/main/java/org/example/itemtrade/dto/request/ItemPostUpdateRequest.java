package org.example.itemtrade.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.example.itemtrade.domain.ItemPost;

@Setter
@Getter
public class ItemPostUpdateRequest {
    private String title;
    private String description;
    private int price;
    private String category;

  public void applyTo(ItemPost post) {
        post.setTitle(this.title);
        post.setDescription(this.description);
        post.setPrice(this.price);
        post.setCategory(this.category);
    }
}