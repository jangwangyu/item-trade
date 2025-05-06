package org.example.itemtrade.repository;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByProviderAndProviderId(String provider, String providerId); // 소셜 로그인 시 사용되는 정보
  Optional<Member> findByEmail(String email);

  // 일반 폼 로그인 시 삭제되지 않은 사용자만 찾기
  Optional<Member> findByEmailAndDeletedFalse(String email);
  // 소셜 로그인 시 삭제되지 않은 사용자만 찾기
  Optional<Member> findByProviderAndProviderIdAndDeletedFalse(String provider, String providerId);

  List<Member> findByDeletedFalse();
}
