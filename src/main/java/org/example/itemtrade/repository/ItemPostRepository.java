package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.enums.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPostRepository extends JpaRepository<ItemPost, Long> {
  List<ItemPost> findAllBySeller(Member seller);
  List<ItemPost> findAllByTitleContaining(String keyword); // 검색

  List<ItemPost> findAllBySellerIn(List<Member> blockedMembers);

  // 판매 완료 수
  int countBySellerAndStatus(Member seller, TradeStatus status);

  // 구매 완료 수
  int countByBuyerAndStatus(Member buyer, TradeStatus status);
}
