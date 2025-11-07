package com.clc.levelup.service;

import com.clc.levelup.dto.LoginRequest;

/**
 * Defines minimal authentication operations for the application.
 * <p>
 * This interface is used before full Spring Security integration.
 * It supports simple credential verification and session principal creation.
 * </p>
 */
public interface AuthService {

  /**
   * Authenticate a login request by comparing submitted credentials
   * against the in-memory or stored user data.
   * @param req login form containing username/email and password
   * @return true if authentication succeeds; false otherwise
   */
  boolean authenticate(LoginRequest req);

  /**
   * Return a simplified principal object (such as a username string)
   * that represents the logged-in user for session tracking.
   * @param emailOrUsername the identifier entered by the user
   * @return principal object stored in session after login
   */
  Object toPrincipal(String emailOrUsername);
}
