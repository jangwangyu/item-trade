package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPostRepository extends JpaRepository<ItemPost, Long> {
  List<ItemPost> findAllBySeller(Member seller); // 마이페이지
  List<ItemPost> findAllByTitleContaining(String keyword); // 검색
}
