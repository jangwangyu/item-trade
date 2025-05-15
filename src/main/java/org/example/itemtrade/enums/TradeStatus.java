package org.example.itemtrade.enums;

public enum TradeStatus {
  TRADE("거래가능"),
  COMPLETE("거래완료"),
  CANCEL("거래취소");

  private final String status;

  TradeStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
