package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  // 특정 게시글에 달린 모든 댓글을 작성 시간 기준 내림차순으로 조회
  List<Comment> findAllByItemPostOrderByCreatedAtDesc(ItemPost post);

  List<Comment> findAllByWriter(Member member);
}
