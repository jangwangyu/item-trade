package org.example.itemtrade.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.enums.TradeStatus;

public record ItemPostResponse(
    Long id,
    String title,
    String description,
    int price,
    Category category,
    boolean isSold,
    LocalDateTime createdAt,
    String sellerNickname,
    Long sellerId,
    List<String> imagePaths,
    TradeStatus tradeStatus) {
  public static ItemPostResponse from(ItemPost post) {
    return new ItemPostResponse(
        post.getId(),
        post.getTitle(),
        post.getDescription(),
        post.getPrice(),
        post.getCategory(),
        post.isSold(),
        post.getCreatedAt(),
        post.getSeller().getNickName(),
        post.getSeller().getId(),
        post.getImages().stream().map(ItemImage::getImagePath).toList(),
        post.getStatus()
    );
  }

  public String getCategoryDisplayName() {
    return category != null ? category.getDisplayName() : "미정";
  }
}
