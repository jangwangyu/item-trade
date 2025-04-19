package org.example.itemtrade.dto.request;

import java.util.List;
import lombok.Getter;

@Getter
public class ItemImageUpdateRequest {
  // TODO:  2개 이상의 이미지일 경우 사용(예정)
  private List<Long> remainImageIds; // 유지할 이미지 ID들(삭제하지 않을 이미지 ID들)

  public void setRemainImageIds(List<Long> remainImageIds) {
    this.remainImageIds = remainImageIds;
  }
}
