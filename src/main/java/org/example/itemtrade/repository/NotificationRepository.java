package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findAllByTarget(Member member);

  List<Notification> findByTargetOrderByCreatedAtDesc(Member member, Pageable pageable);

  Long countByTargetAndIsReadIsFalse(Member member);
}
