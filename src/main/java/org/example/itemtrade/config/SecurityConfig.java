package org.example.itemtrade.config;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final CustomOAuth2UserService customOAuth2UserService;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/oauth2/**", "/ws/**").permitAll()
            .anyRequest().permitAll()
        )
        .oauth2Login(oauth -> oauth
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService) // 여기서 CustomOAuth2User 생성
            )
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/") // ✅ 로그아웃 후 이동할 URL
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
  }
}
