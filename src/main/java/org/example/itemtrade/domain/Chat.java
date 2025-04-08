package org.example.itemtrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Chat {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_id")
  @Id
  private Long id;

  String title;

  LocalDateTime createAt;

  @ManyToOne
  private ItemPost itemPost;

}
