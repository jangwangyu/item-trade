package org.example.itemtrade.service;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;


  public void deleteMember(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    member.delete();
  }

  @Transactional
  public void restoreMember(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    if (!member.isDeleted()) {
      throw new IllegalArgumentException("이미 활성화 된 회원입니다.");
    }

    member.restore();
  }




}
