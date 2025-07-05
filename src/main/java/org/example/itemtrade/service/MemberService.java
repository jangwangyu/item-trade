package org.example.itemtrade.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.Util.JwtTokenProvider;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.example.itemtrade.dto.MemberProfileDto;
import org.example.itemtrade.dto.User.UserType;
import org.example.itemtrade.dto.request.MemberJoinRequest;
import org.example.itemtrade.dto.request.MemberUpdateRequest;
import org.example.itemtrade.dto.response.JwtResponse;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.example.itemtrade.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final MemberBlockRepository memberBlockRepository;
  private final ItemPostRepository itemPostRepository;
  private final JwtTokenProvider jwtTokenProvider;
  // 로그인
  public JwtResponse login(String email, String password) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    if (!passwordEncoder.matches(password, member.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    String token = jwtTokenProvider.createToken(member.getEmail(), member.getId());
    return new JwtResponse(token);
  }

  // 회원 가입
  public void join(MemberJoinRequest request) {
    String email = request.getEmail().trim();
    String password = request.getPassword().trim();
    String confirmPassword = request.getConfirmPassword().trim();
    String nickname = request.getNickname().trim();

    if (memberRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }
    if (!password.equals(confirmPassword)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    if (memberRepository.existsByNickName(nickname)) {
      throw new IllegalArgumentException("이미 가입된 닉네임입니다.");
    }
    if (nickname.length() < 2 || nickname.length() > 10) {
      throw new IllegalArgumentException("닉네임은 2자 이상 10자 이하로 입력해주세요.");
    }
    if (password.length() < 8 || password.length() > 20) {
      throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하로 입력해주세요.");
    }
    String encodedPassword = passwordEncoder.encode(password);
    Member memberJoin = request.toEntity(encodedPassword);

    String seed = UUID.randomUUID().toString();
    String profileImageUrl = "https://api.dicebear.com/7.x/adventurer/svg?seed=" + seed + ".svg";

    memberJoin.setProfileImageUrl(profileImageUrl);

    memberRepository.save(memberJoin);
  }

  // 회원 수정
  public void updateMember(Member member, MemberUpdateRequest memberUpdateRequest, UserType userType) {
    Member currentUser = getMemberId(member.getId());

    if("FORM".equals(userType.getLoginType())) {
      currentUser.setNickName(memberUpdateRequest.getNickName());
    }

    if (memberUpdateRequest.getIntroduction() != null) {
      currentUser.setIntroduction(memberUpdateRequest.getIntroduction());
    }

    currentUser.setProfileImageUrl(memberUpdateRequest.getProfileImageUrl());

    memberRepository.save(currentUser);
  }

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

  // 회원 차단 여부 확인
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

  // 차단된 회원 목록 조회
  public List<Member> getBlockedMembers(Member member) {
    return memberBlockRepository.findAllByBlocker(member).stream()
        .map(MemberBlock::getBlocked)
        .toList();
  }
  // 상대방 조회
  public MemberProfileDto getMemberProfile(Long memberId) {
    Member targetMember = getMemberId(memberId);
    int sellCount = itemPostRepository.countBySellerAndStatus(targetMember, TradeStatus.COMPLETE);
    int buyCount = itemPostRepository.countByBuyerAndStatus(targetMember, TradeStatus.COMPLETE);
    int totalCount = sellCount + buyCount;

    return MemberProfileDto.from(targetMember, totalCount);
  }
}
