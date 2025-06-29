package org.example.itemtrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.Util.SoftDeleteUtil;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.repository.ChatMessageRepository;
import org.example.itemtrade.repository.ChatRoomRepository;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemImageRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.LikeRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MyPageService {

  private final ItemPostRepository itemPostRepository;
  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final ItemImageRepository itemImageRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final LikeRepository likeRepository;

  // 내가 작성한 게시글 조회
  public List<ItemPostResponse> getMyPosts(Member member) {
    Member user = findById(member.getId());

    return itemPostRepository.findAllBySeller(user)
        .stream()
        .map(ItemPostResponse::from)
        .toList();
  }

  // 찜한 게시글 조회
  public List<ItemPostResponse> getMyLikes(Member member) {
    Member user = findById(member.getId());
    List<ItemPost> likedPosts = likeRepository.findLikedByMember(user);

    return likedPosts.stream()
        .map(post -> ItemPostResponse.from(post, true)).toList();
  }


  // 회원 탈퇴
  public void deleteMember(Member member) {
    Member user = findById(member.getId());

    SoftDeleteUtil.softDeleteAll(itemPostRepository.findAllBySeller(user));
    SoftDeleteUtil.softDeleteAll(commentRepository.findAllByWriter(user));

    List<ItemPost> posts = itemPostRepository.findAllBySeller(user);
    List<ItemImage> allImages = itemImageRepository.findAllByItemPostIn(posts);
    SoftDeleteUtil.softDeleteAll(allImages);

    SoftDeleteUtil.softDeleteAll(chatMessageRepository.findAllBySender(user));
    SoftDeleteUtil.softDeleteAll(chatRoomRepository.findAllByBuyerAndSeller(user, user));

    user.setDeleted(true);
    memberRepository.save(user);
  }

  // findById
  public Member findById(Long id) {
    return memberRepository.findById(id).orElseThrow(() ->
        new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }
}
