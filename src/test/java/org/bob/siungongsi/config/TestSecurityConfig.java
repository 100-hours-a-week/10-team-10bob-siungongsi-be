package org.bob.siungongsi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

/** 테스트 전용 보안 구성으로, 컨트롤러 테스트를 위해 Spring Security를 완전히 비활성화합니다. */
@ActiveProfiles({"test"})
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

  @Bean
  @Primary
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .httpBasic()
        .disable()
        .formLogin()
        .disable()
        .logout()
        .disable()
        .sessionManagement()
        .disable()
        .requestCache()
        .disable()
        .securityContext()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .permitAll();

    return http.build();
  }
}
