package org.example.itemtrade.dto;


import java.time.LocalDateTime;
import org.example.itemtrade.domain.ChatMessage;

public record ChatMessageDto(
    Long id,
    String content,
    String senderNickname,
    String senderProfileImageUrl,
    boolean isRead,
    LocalDateTime createdAt,
    Long senderId,
    String type
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
            message.getId(),
            message.getContent(),
            message.getSender().getNickName(),
            message.getSender().getProfileImageUrl(),
            message.isRead(),
            message.getCreatedAt(),
            message.getSender().getId(),
            message.getType()
        );
    }
}