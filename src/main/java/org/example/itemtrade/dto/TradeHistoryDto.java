package org.example.itemtrade.dto;


import java.time.LocalDateTime;
import org.example.itemtrade.domain.TradeHistory;

public record TradeHistoryDto(
    Long id,
    String buyerNickname,
    String sellerNickname,
    String itemTitle,
    int price,
    LocalDateTime createAt
) {
    public static TradeHistoryDto from(TradeHistory trade) {
        return new TradeHistoryDto(
            trade.getId(),
            trade.getBuyer().getNickName(),
            trade.getSeller().getNickName(),
            trade.getItemPost().getTitle(),
            trade.getItemPost().getPrice(),
            trade.getCreateAt()
        );
    }

}