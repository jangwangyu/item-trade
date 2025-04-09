package org.example.itemtrade.repository;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  // 구매자 채팅방 목록 조회
  List<ChatRoom> findAllByBuyer(Member buyer);
  // 판매자 참여한 채팅방 목록 조회
  List<ChatRoom> findAllBySeller(Member seller);
  // 해당 아이템 게시글에 대해 구매자가 이미 만든 채팅방이 있는지 확인 (중복 방지용)
  Optional<ChatRoom> findByItemPostAndBuyer(ItemPost post, Member buyer);
}
