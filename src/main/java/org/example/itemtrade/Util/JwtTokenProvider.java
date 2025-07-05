package org.example.itemtrade.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  @Value("${jwt.secret}")
  private String secretKey;
  private final Long tokenValidityInMilliseconds = 1000L * 60 * 60; // 1 hour

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  // JWT 토큰 생성
  public String createToken(String username, Long userId) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("userId", userId);

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + tokenValidityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // JWT 토큰에서 사용자 정보 추출
  public String getUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false; // 토큰이 유효하지 않거나 만료된 경우
    }
  }
}
