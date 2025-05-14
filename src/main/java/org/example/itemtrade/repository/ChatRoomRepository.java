package org.example.itemtrade.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ChatRoom;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  // 구매자 채팅방 목록 조회
  List<ChatRoom> findAllByBuyer(Member buyer);

  // 판매자 참여한 채팅방 목록 조회
  List<ChatRoom> findAllBySeller(Member seller);

  Collection<ChatRoom> findAllByBuyerAndSeller(Member buyer, Member seller);
  // 해당 아이템 게시글에 대해 구매자가 이미 만든 채팅방이 있는지 확인 (중복 방지용)
  Optional<ChatRoom> findByItemPostAndBuyerAndSeller(ItemPost post, Member buyer, Member seller);

  // 구매자와 판매자 모두 삭제한 채팅방 목록 조회
  List<ChatRoom> findAllByBuyerAndDeletedByBuyerFalse(Member buyer);
  List<ChatRoom> findAllBySellerAndDeletedBySellerFalse(Member seller);

  // 마지막 메세지로 정렬
  @Query("SELECT cr FROM ChatRoom cr WHERE (cr.buyer = :member AND cr.deletedByBuyer=false) OR (cr.seller = :member AND cr.deletedBySeller=false) ORDER BY cr.lastMessageCreatedAt DESC")
  // Chatroom을 cr로 alias하고 cr에 있는 buyer가 member에 있고 deletedByBuyer가 false이거나 seller가 member에 있고 deletedBySeller가 false일 경우 lastMessageCreatedAt으로 정렬
  List<ChatRoom> findAllByMemberOrderByLastMessage(@Param("member")Member member);
}
