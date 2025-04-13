package org.example.itemtrade.repository;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  Optional<Member> findByEmail(String email);
  Optional<Member> findByEmailAndDeletedFalse(String email);

  List<Member> findByDeletedFalse();
}
