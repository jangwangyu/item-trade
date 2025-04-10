package org.example.itemtrade.dto.request;

import java.time.LocalDateTime;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;

public class ChatMessageRequest {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChatMessage toEntity(Member sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
            .content(this.content)
            .sender(sender)
            .chat(chatRoom)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
    }
}