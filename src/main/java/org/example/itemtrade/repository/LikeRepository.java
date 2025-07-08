package org.example.itemtrade.repository;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Like;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
  Optional<Like> findByMemberAndItemPost(Member member, ItemPost itemPost);
  boolean existsByMemberAndItemPost(Member member, ItemPost itemPost);
  void deleteByMemberAndItemPost(Member member, ItemPost itemPost);

  @Query("SELECT l.itemPost FROM Like l WHERE l.member = :member")
  List<ItemPost> findLikedByMember(@Param("member") Member member);

}
