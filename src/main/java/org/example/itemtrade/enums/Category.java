package org.example.itemtrade.enums;

public enum Category {
  ACTION("액션"),
  RPG("롤플레잉"),
  FPS("슈팅"),
  ADVENTURE("모험"),
  ROGUELIKE("로그라이크");

  private final String displayName;

  Category(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() { // ★ 반드시 public
    return displayName;
  }
}
