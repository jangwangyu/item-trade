package org.example.itemtrade.service;

import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class MockOAuth2UserRequest extends OAuth2UserRequest {
  private final OAuth2User oAuth2User;

  public MockOAuth2UserRequest(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
    super(userRequest.getClientRegistration(), userRequest.getAccessToken(), userRequest.getAdditionalParameters());
    this.oAuth2User = oAuth2User;
  }

  @Override
  public Map<String, Object> getAdditionalParameters() {
    return oAuth2User.getAttributes();
  }
}
