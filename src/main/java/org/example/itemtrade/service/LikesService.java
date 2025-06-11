package org.example.itemtrade.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Like;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LikesService {

  private final LikeRepository likeRepository;

  // 좋아요 추가
  public boolean toggleLikes(Member member, ItemPost itemPost) {
    Optional<Like> existingLike = likeRepository.findByMemberAndItemPost(member, itemPost);
    if(existingLike.isPresent()) {
      // 좋아요가 있다면 삭제
      likeRepository.deleteByMemberAndItemPost(member, itemPost);
      itemPost.setLikeCount(itemPost.getLikeCount() - 1);
      return false;
    } else {
      // 좋아요가 없다면 추가
      Like like = new Like();
      like.setMember(member);
      like.setItemPost(itemPost);
      likeRepository.save(like);
      itemPost.setLikeCount(itemPost.getLikeCount() + 1);
      return true;
    }
  }
}
