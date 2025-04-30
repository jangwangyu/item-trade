package org.example.itemtrade.dto;


import org.example.itemtrade.domain.ChatRoom;

public record ChatRoomDto(
    Long id,
    Long itemId,
    String itemTitle,
    Integer itemPrice,
    String buyerNickname,
    String sellerNickname,
    Long unreadCount,
    String lastMessage
)

{

    public static ChatRoomDto from(ChatRoom chatRoom, Long unreadCount, String lastMessage) {
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
            lastMessage
        );
    }
}