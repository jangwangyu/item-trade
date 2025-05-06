package org.example.itemtrade.dto.Oauth2;

import org.example.itemtrade.domain.Member;

public interface UserType {
  Member getMember();
  String getLoginType();
}
