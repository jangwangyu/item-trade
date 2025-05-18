package org.example.itemtrade.dto.User;

import org.example.itemtrade.domain.Member;

public interface UserType {
  Member getMember();
  String getLoginType();
}
