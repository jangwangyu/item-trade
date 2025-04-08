package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ChatMessage {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_message_id")
  @Id
  private Long id;

  @ManyToOne
  private Chat chat;

  @ManyToOne
  private Member sender;

  private String content;

  private boolean isRead;
  private LocalDateTime createdAt;

}
