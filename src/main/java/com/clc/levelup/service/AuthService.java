package com.clc.levelup.service;

import com.clc.levelup.dto.LoginRequest;

// Minimal auth for M2 (no Spring Security yet).
public interface AuthService {
  boolean authenticate(LoginRequest req);          // compare against in-memory store
  Object toPrincipal(String emailOrUsername);      // what we put in session later
}
