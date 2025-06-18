package org.example.itemtrade.repository;

import java.util.Collection;
import java.util.List;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.enums.TradeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemPostRepository extends JpaRepository<ItemPost, Long> {
  // 전체조회
  @Query("""
  SELECT p FROM ItemPost p
  WHERE (:category IS NULL OR p.category = :category)
  AND (:minPrice IS NULL OR p.price >= :minPrice)
  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
  AND (:blockedMembers IS NULL OR p.seller NOT IN :blockedMembers)
""")
  Page<ItemPost> findAllPosts(@Param("category") Category category,
                                   @Param("minPrice") Integer minPrice,
                                   @Param("maxPrice") Integer maxPrice,
                                   @Param("blockedMembers") List<Member> blockedMembers,
                                   Pageable pageable);
  List<ItemPost> findAllBySeller(Member seller);
  List<ItemPost> findAllByTitleContaining(String keyword); // 검색

  List<ItemPost> findAllBySellerIn(List<Member> blockedMembers);

  // 판매 완료 수
  int countBySellerAndStatus(Member seller, TradeStatus status);

  // 구매 완료 수
  int countByBuyerAndStatus(Member buyer, TradeStatus status);
}
