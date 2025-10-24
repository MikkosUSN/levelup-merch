// src/main/java/com/clc/levelup/config/SecurityConfig.java
package com.clc.levelup.config;

import com.clc.levelup.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * HTTP security rules, form login, and password encoder.
 */
@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider(CustomUserDetailsService uds, PasswordEncoder encoder) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(uds);
    p.setPasswordEncoder(encoder);
    // Update: avoid leaking whether the username/email existed
    p.setHideUserNotFoundExceptions(true);
    return p;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
    http
      .authenticationProvider(authProvider)
      .authorizeHttpRequests(auth -> auth
        .antMatchers("/", "/login", "/register", "/forgot", "/reset/**", "/css/**", "/js/**", "/images/**").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(login -> login
        .loginPage("/login")
        // Update: our login form posts "emailOrUsername" and "password"
        .usernameParameter("emailOrUsername")
        .passwordParameter("password")
        .defaultSuccessUrl("/products", true)
        .failureUrl("/login?error")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .permitAll()
      )
      .csrf(); // keep CSRF on

    return http.build();
  }
}
