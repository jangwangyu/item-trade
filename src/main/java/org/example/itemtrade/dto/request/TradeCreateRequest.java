package org.example.itemtrade.dto.request;


import java.time.LocalDateTime;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.TradeHistory;

public class TradeCreateRequest {

    public TradeHistory toEntity(Member buyer, Member seller, ItemPost itemPost) {
        return TradeHistory.builder()
            .buyer(buyer)
            .seller(seller)
            .itemPost(itemPost)
            .createAt(LocalDateTime.now())
            .build();
    }
}