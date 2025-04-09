package org.example.itemtrade.repository;

import java.util.List;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.springframework.data.repository.CrudRepository;

public interface ItemImageRepository extends CrudRepository<ItemImage, Long> {
  // 특정 게시글(ItemPost)에 연결된 모든 이미지 리스트 조회
  List<ItemImage> findAllByItemPost(ItemPost post);
}
