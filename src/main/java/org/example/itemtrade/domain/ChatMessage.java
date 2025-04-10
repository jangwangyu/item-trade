package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ChatMessage {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  @Id
  private Long id;

  @ManyToOne
  private ChatRoom chat;

  @ManyToOne
  private Member sender;

  private String content;

  private boolean isRead;
  private LocalDateTime createdAt;

  public static ChatMessage of(Member sender, ChatRoom chatRoom, String content) {
    ChatMessage message = new ChatMessage();
    message.sender = sender;
    message.chat = chatRoom;
    message.content = content;
    message.isRead = false;
    message.createdAt = LocalDateTime.now();
    return message;
  }

}
