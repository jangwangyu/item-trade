package org.example.itemtrade.dto;


import java.util.Objects;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.enums.TradeStatus;

public record ChatRoomDto(
    Long id,
    Long itemId,
    String itemTitle,
    Integer itemPrice,
    String buyerNickname,
    String sellerNickname,
    Long unreadCount,
    String lastMessage,
    Long opponentId,
    boolean isBlocked,
    TradeStatus tradeStatus,
    boolean isTradeSellerComplete,
    boolean isTradeBuyerComplete,
    Long buyerId,
    Long sellerId,
    String sellerProfileImageUrl

)

{

    public static ChatRoomDto from(ChatRoom chatRoom, Long unreadCount, String lastMessage,Long opponentId, boolean isBlocked) {
        // 구매자와 판매자가 null일 수 있으므로 null 체크 후 기본값 설정
        String buyerNickname = (chatRoom.getBuyer() != null) ? chatRoom.getBuyer().getNickName() : "Unknown";
        String sellerNickname = (chatRoom.getSeller() != null) ? chatRoom.getSeller().getNickName() : "Unknown";

        return new ChatRoomDto(
            chatRoom.getId(),
            chatRoom.getItemPost().getId(),
            chatRoom.getItemPost().getTitle(),
            chatRoom.getItemPost().getPrice(),
            buyerNickname,
            sellerNickname,
            unreadCount,
            lastMessage,
            opponentId,
            isBlocked,
            chatRoom.getTradeStatus(),
            chatRoom.isTradeSellerComplete(),
            chatRoom.isTradeBuyerComplete(),
            Objects.requireNonNull(chatRoom.getBuyer()).getId(),
            Objects.requireNonNull(chatRoom.getSeller()).getId(),
            Objects.requireNonNull(chatRoom.getSeller()).getProfileImageUrl()
        );
    }
}