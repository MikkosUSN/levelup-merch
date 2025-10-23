package com.clc.levelup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * HTTP security rules, form login, and password encoder.
 */
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        // Update (M6): allow password reset endpoints
        .antMatchers("/", "/login", "/register", "/forgot", "/reset/**", "/css/**", "/js/**", "/images/**").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(login -> login
        .loginPage("/login")
        .defaultSuccessUrl("/products", true)
        .failureUrl("/login?error")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .permitAll()
      )
      .csrf(); // keep CSRF enabled

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
