package org.example.itemtrade.dto;


import org.example.itemtrade.domain.ChatRoom;

public record ChatRoomDto(
    Long id,
    Long itemId,
    String itemTitle,
    Integer itemPrice,
    String buyerNickname,
    String sellerNickname,
    Long unreadCount
) {
    public static ChatRoomDto from(ChatRoom chatRoom, Long unreadCount) {
        return new ChatRoomDto(
            chatRoom.getId(),
            chatRoom.getItemPost().getId(),
            chatRoom.getItemPost().getTitle(),
            chatRoom.getItemPost().getPrice(),
            chatRoom.getBuyer().getNickName(),
            chatRoom.getSeller().getNickName(),
            unreadCount
        );
    }
}