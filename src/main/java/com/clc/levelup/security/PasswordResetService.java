package com.clc.levelup.security;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clc.levelup.model.User;
import com.clc.levelup.repository.UserRepository;

/*
 * Creates and validates reset tokens. Resets password using BCrypt.
 */
@Service
public class PasswordResetService {

  private final UserRepository users;
  private final PasswordResetTokenRepository tokens;
  private final PasswordEncoder encoder;
  private final SecureRandom random = new SecureRandom();
  private final HexFormat hex = HexFormat.of();

  public PasswordResetService(UserRepository users,
                              PasswordResetTokenRepository tokens,
                              PasswordEncoder encoder) {
    this.users = users;
    this.tokens = tokens;
    this.encoder = encoder;
  }

  public Optional<PasswordResetToken> createTokenForEmail(String email) {
    var userOpt = users.findByEmail(email);
    if (userOpt.isEmpty()) return Optional.empty();

    var token = new PasswordResetToken();
    token.setUserId(userOpt.get().getId());
    token.setToken(generateToken());
    token.setExpiresAt(LocalDateTime.now().plusMinutes(30)); // 30 min window
    token.setUsed(0);
    return Optional.of(tokens.save(token));
  }

  public Optional<User> validateToken(String tokenValue) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return Optional.empty();
    var t = tokOpt.get();
    if (t.getUsed() != null && t.getUsed() == 1) return Optional.empty();
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return Optional.empty();
    return users.findById(t.getUserId());
  }

  public boolean resetPassword(String tokenValue, String rawNewPassword) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return false;
    var t = tokOpt.get();
    if (t.getUsed() != null && t.getUsed() == 1) return false;
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return false;

    var userOpt = users.findById(t.getUserId());
    if (userOpt.isEmpty()) return false;

    var user = userOpt.get();
    user.setPassword(encoder.encode(rawNewPassword));
    users.save(user);

    t.setUsed(1);
    tokens.save(t);
    return true;
  }

  private String generateToken() {
    byte[] buf = new byte[24]; // 48 hex chars
    random.nextBytes(buf);
    return hex.formatHex(buf);
  }
}
