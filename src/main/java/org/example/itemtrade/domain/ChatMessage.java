package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.itemtrade.dto.SoftDelete;
import org.hibernate.annotations.CreationTimestamp;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Entity
public class ChatMessage implements SoftDelete {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  @Id
  private Long id;

  @ManyToOne
  private ChatRoom chatRoom;

  @ManyToOne
  private Member sender;

  private String content;

  private String type;

  private boolean isRead;

  @CreationTimestamp
  private LocalDateTime createdAt;

  private boolean deleted = false;


  public ChatMessage(Member sender, ChatRoom chatRoom, String content, String type) {
    this.sender = sender;
    this.chatRoom = chatRoom;
    this.content = content;
    this.type = type;
    this.isRead = false;
    this.createdAt = LocalDateTime.now();
  }

  public static ChatMessage of(Member sender, ChatRoom chatRoom, String content, String type) {
    ChatMessage message = new ChatMessage();
    message.sender = sender;
    message.chatRoom = chatRoom;
    message.content = content;
    message.isRead = false;
    message.type = type;
    message.createdAt = LocalDateTime.now();
    return message;
  }

  @Override
  public void softDelete() {
    this.deleted = true;
  }

  @Override
  public boolean isDeleted() {
    return this.deleted;
  }
}
