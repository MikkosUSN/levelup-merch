package com.clc.levelup.service;

import com.clc.levelup.dto.LoginRequest;
import com.clc.levelup.model.User;
import org.springframework.stereotype.Service;

// Checks login against the users we stored in memory.
@Service
public class InMemoryAuthService implements AuthService {

  private final UserService users;

  public InMemoryAuthService(UserService users) {
    this.users = users;
  }

  @Override
  public boolean authenticate(LoginRequest req) {
    return users.findByEmailOrUsername(req.getEmailOrUsername())
        .map(u -> ("{noop}" + req.getPassword()).equals(u.getPasswordHash()))
        .orElse(false);
  }

  @Override
  public Object toPrincipal(String key) {
    // Keep it simple: store the username as the "principal" in session later
    User u = users.findByEmailOrUsername(key).orElseThrow();
    return u.getUsername();
  }
}
