package org.example.itemtrade.repository;

import java.util.List;
import java.util.Optional;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long> {
  boolean existsByBlockerAndBlocked(Member memberBlock, Member blocked);

  List<MemberBlock> findAllByBlocker(Member blocker);

  Optional<MemberBlock> findByBlockerAndBlocked(Member blocker, Member blocked);
}
