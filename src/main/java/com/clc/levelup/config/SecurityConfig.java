package com.clc.levelup.config;

import com.clc.levelup.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Defines security rules for both web and API layers.
 * Handles form login for the UI and Basic authentication for REST endpoints.
 * Passwords are encrypted using BCrypt.
 */
@Configuration
public class SecurityConfig {

  /**
   * Provides a password encoder bean using BCrypt hashing.
   * Used to encrypt and verify user passwords in authentication.
   * @return PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configure authentication provider with a custom UserDetailsService.
   * Connects application user data to Spring Security for login validation.
   * @param uds injected CustomUserDetailsService for user lookup
   * @param encoder injected PasswordEncoder for password validation
   * @return configured DaoAuthenticationProvider
   */
  @Bean
  public DaoAuthenticationProvider authProvider(CustomUserDetailsService uds, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(uds);
    provider.setPasswordEncoder(encoder);
    // Prevents disclosure of whether a user exists
    provider.setHideUserNotFoundExceptions(true);
    return provider;
  }

  /**
   * Configure all HTTP security rules including route permissions,
   * form login, logout, and Basic authentication for APIs.
   * @param http HttpSecurity builder
   * @param authProvider authentication provider for user verification
   * @return built SecurityFilterChain
   * @throws Exception if security configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
    http
      // Register custom authentication provider
      .authenticationProvider(authProvider)
      .authorizeRequests(auth -> auth
        // Public routes and static assets
        .antMatchers("/", "/login", "/register", "/forgot", "/reset/**",
                     "/css/**", "/js/**", "/images/**",
                     "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

        // Allow read-only browsing of products without login
        .antMatchers(HttpMethod.GET, "/products/**").permitAll()

        // API endpoints require ROLE_API and Basic authentication
        .antMatchers("/api/**").hasRole("API")

        // Require login for cart, checkout, and order operations
        .antMatchers("/cart/**", "/checkout", "/orders").authenticated()

        // Default: any other route requires authentication
        .anyRequest().authenticated()
      )
      // Enable Basic authentication for API access
      .httpBasic(Customizer.withDefaults())
      // Configure form login for web users
      .formLogin(login -> login
        .loginPage("/login")
        .usernameParameter("emailOrUsername") // custom field name
        .passwordParameter("password")
        .defaultSuccessUrl("/products", true)
        .failureUrl("/login?error")
        .permitAll()
      )
      // Configure logout handling
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .permitAll()
      )
      // Keep CSRF enabled; safe for GET APIs
      .csrf();

    // Build and return the security filter chain
    return http.build();
  }
}
