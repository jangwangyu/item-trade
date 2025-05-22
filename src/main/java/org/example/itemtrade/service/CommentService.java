package org.example.itemtrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Comment;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.CommentDto;
import org.example.itemtrade.dto.request.CommentCreateRequest;
import org.example.itemtrade.repository.CommentRepository;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final ItemPostRepository itemPostRepository;
  private final MemberRepository memberRepository;


  /**
   * 댓글 조회
   *
   * @return 댓글 정보
   */

  public Page<CommentDto> commentList(Long postId, Pageable pageable) {
    ItemPost itemPost = itemPostRepository.findById(postId).orElseThrow(() ->
        new IllegalArgumentException("게시물이 존재하지 않습니다."));
    Page<Comment> comments = commentRepository.findAllByItemPostOrderByCreatedAtDesc(itemPost, pageable);

    return comments.map(CommentDto::from);
  }

  /**
   * 댓글 작성
   *
   * @param request 댓글 작성 요청
   * @param postId  게시물 ID
   * @param member  작성자
   * @return 작성된 댓글 정보
   */
  public CommentDto addComment(CommentCreateRequest request, Long postId, Member member) {
    ItemPost itemPost = itemPostRepository.findById(postId).orElseThrow(() ->
        new IllegalArgumentException("게시물이 존재하지 않습니다."));
    Member writer = memberRepository.findById(member.getId()).orElseThrow(() ->
        new IllegalArgumentException("작성자가 존재하지 않습니다."));
    Comment comment = Comment.of(writer, itemPost, request.content());
    commentRepository.save(comment);
    return CommentDto.from(comment);
  }

  /**
   * 댓글 삭제
   *
   * @param commentId 댓글 ID
   * @param member    삭제 요청자
   */
  public void deleteComment(Long commentId, Member member) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
        new IllegalArgumentException("댓글이 존재하지 않습니다."));
    if(!comment.getWriter().getId().equals(member.getId())) {
      throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
    }

    ItemPost itemPost = comment.getItemPost();
    Long postId = itemPost.getId();
    commentRepository.delete(comment);

  }
}
