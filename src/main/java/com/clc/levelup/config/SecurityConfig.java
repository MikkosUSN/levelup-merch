package com.clc.levelup.config;

import com.clc.levelup.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Update: method-based rules for GET products
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * HTTP security rules, form login, and password encoder.
 * Simple update: add Basic Auth for /api/** with ROLE_API, keep the UI flow.
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
    // avoid revealing whether user exists
    p.setHideUserNotFoundExceptions(true);
    return p;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
    http
      .authenticationProvider(authProvider)
      .authorizeRequests(auth -> auth
        .antMatchers("/", "/login", "/register", "/forgot", "/reset/**",
                     "/css/**", "/js/**", "/images/**",
                     // allow Swagger UI when present
                     "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

        // Update: allow anonymous browsing of products (GET only)
        .antMatchers(HttpMethod.GET, "/products/**").permitAll()

        // APIs require ROLE_API via Basic
        .antMatchers("/api/**").hasRole("API")

        // Update: require login for cart/checkout/orders and any other POST to products
        .antMatchers("/cart/**", "/checkout", "/orders").authenticated()

        .anyRequest().authenticated()
      )
      // Basic for APIs
      .httpBasic(Customizer.withDefaults())
      // Form login for UI
      .formLogin(login -> login
        .loginPage("/login")
        // login form fields
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
      // keep CSRF. APIs are safe if they’re GETs.
      .csrf();

    return http.build();
  }
}
