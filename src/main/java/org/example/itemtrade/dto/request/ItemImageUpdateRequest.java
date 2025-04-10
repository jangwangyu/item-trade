package org.example.itemtrade.dto.request;

import java.util.List;
import lombok.Getter;

@Getter
public class ItemImageUpdateRequest {
  private List<Long> remainImageIds; // 유지할 이미지 ID들

  public void setRemainImageIds(List<Long> remainImageIds) {
    this.remainImageIds = remainImageIds;
  }
}
