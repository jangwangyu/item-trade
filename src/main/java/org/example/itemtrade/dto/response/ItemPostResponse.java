package org.example.itemtrade.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;

public record ItemPostResponse(
    Long id,
    String title,
    String description,
    int price,
    String category,
    boolean isSold,
    LocalDateTime createdAt,
    String sellerNickname,
    Long sellerId,
    String imagePath
) {
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
        post.getImagePath()
    );
  }
}
