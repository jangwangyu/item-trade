package org.example.itemtrade.dto;


import java.time.LocalDateTime;
import org.example.itemtrade.domain.ChatMessage;

public record ChatMessageDto(
    Long id,
    String content,
    String senderNickname,
    boolean isRead,
    LocalDateTime createdAt,
    Long senderId
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
            message.getId(),
            message.getContent(),
            message.getSender().getNickName(),
            message.isRead(),
            message.getCreatedAt(),
            message.getSender().getId()
        );
    }
}