package org.example.itemtrade.dto;


import org.example.itemtrade.domain.ChatRoom;

public record ChatRoomDto(
    Long id,
    Long itemId,
    String itemTitle,
    String buyerNickname,
    String sellerNickname
) {
    public static ChatRoomDto from(ChatRoom chatRoom) {
        return new ChatRoomDto(
            chatRoom.getId(),
            chatRoom.getItemPost().getId(),
            chatRoom.getItemPost().getTitle(),
            chatRoom.getBuyer().getNickName(),
            chatRoom.getSeller().getNickName()
        );
    }
}