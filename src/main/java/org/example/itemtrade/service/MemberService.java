package org.example.itemtrade.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final MemberBlockRepository memberBlockRepository;

  // 회원 차단
  public void blockMember(Member blocker, Member blocked) {
    Member currentUser = getMember(blocker);
    Member targetUser = getMember(blocked);

    if(memberBlockRepository.existsByBlockerAndBlocked(currentUser, targetUser)) {
      throw new IllegalArgumentException("이미 차단된 회원입니다.");
    }
    MemberBlock memberBlock = MemberBlock.of(currentUser, targetUser);
    memberBlockRepository.save(memberBlock);
  }



  // 회원 차단 해제
  public void unBlockMember(Member blocker, Member blocked) {
    Member currentUser = getMember(blocker);
    Member targetUser = getMember(blocked);
    MemberBlock memberBlock = memberBlockRepository.findByBlockerAndBlocked(currentUser, targetUser)
        .orElseThrow(() -> new IllegalArgumentException("차단된 회원이 아닙니다."));
    memberBlockRepository.delete(memberBlock);
  }

  public Boolean isMemberBlocked(Member blocker, Member blocked) {
    return memberBlockRepository.existsByBlockerAndBlocked(blocker, blocked);
  }
  
  // 회원 조회
  public Member getMember(Member member) {
    return memberRepository.findById(member.getId()).orElseThrow(() ->
        new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }

  public Member getMemberId(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(() ->
        new IllegalArgumentException("사용자를 찾을 수 없습니다."));
  }

  public List<Member> getBlockedMembers(Member member) {
    return memberBlockRepository.findAllByBlocker(member).stream()
        .map(MemberBlock::getBlocked)
        .toList();
  }
}
