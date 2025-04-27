package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.ChatMessage;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  // 채팅방에 속한 메시지를 생성 시간 기준으로 오름차순 정렬해서 가져오기
  List<ChatMessage> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom room);
  // 현재 사용자가 받은 안 읽은 메시지 개수 조회
  long countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom room, Member currentUser);
}
