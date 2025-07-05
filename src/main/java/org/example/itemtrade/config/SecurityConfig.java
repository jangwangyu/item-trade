package org.example.itemtrade.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.Util.JwtAuthenticationFilter;
import org.example.itemtrade.Util.JwtTokenProvider;
import org.example.itemtrade.service.CustomOAuth2UserService;
import org.example.itemtrade.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomUserDetailsService customUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT를 사용하므로 세션을 사용하지 않음
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/","login", "/oauth2/**", "/ws/**","/images/**","/chat.js", "/register/**","/api/register", "api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
            }))
        .userDetailsService(customUserDetailsService)
        .oauth2Login(oauth -> oauth
            .defaultSuccessUrl("/", true)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService) // 여기서 CustomOAuth2User 생성
            )
        )
        .logout(logout -> logout
            .logoutUrl("/logout") // 로그아웃 URL
            .logoutSuccessUrl("/login?logout")
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
