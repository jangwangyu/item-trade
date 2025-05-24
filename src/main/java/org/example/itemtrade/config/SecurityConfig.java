package org.example.itemtrade.config;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.dto.User.CustomUserDetails;
import org.example.itemtrade.service.CustomOAuth2UserService;
import org.example.itemtrade.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomUserDetailsService customUserDetailsService;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login/**", "/login-form", "/oauth2/**", "/ws/**","/images/**","/chat.js", "/register/**","/api/register" ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.sendRedirect("/login-form?needAuth=true");
            }))
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true) // 로그인 성공 후 이동할 URL
            .failureUrl("/login-form?error") // 로그인 실패 시 이동할 URL
            .permitAll()
        )
        .userDetailsService(customUserDetailsService)
        .oauth2Login(oauth -> oauth
            .defaultSuccessUrl("/", true)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService) // 여기서 CustomOAuth2User 생성
            )
        )
        .logout(logout -> logout
            .logoutUrl("/logout") // 로그아웃 URL
            .logoutSuccessUrl("/login-form?logout") // ✅ 로그아웃 후 이동할 URL
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
