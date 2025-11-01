package com.clc.levelup.security;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clc.levelup.model.User;
import com.clc.levelup.repository.UserRepository;

/*
 * Creates and validates reset tokens, and updates passwords using BCrypt.
 */
@Service
public class PasswordResetService {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

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

  /** Kept for compatibility when called by email only. */
  public Optional<PasswordResetToken> createTokenForEmail(String email) {
    return createTokenInternal(users.findByEmail(safe(email)));
  }

  /**
   * Accepts either email or username and creates a reset token if the user exists.
   */
  public Optional<PasswordResetToken> createTokenForIdentifier(String identifier) {
    String id = safe(identifier);
    Optional<User> userOpt;

    if (id.contains("@")) {
      userOpt = users.findByEmail(id);
      if (!userOpt.isPresent()) {
        userOpt = users.findByUsername(id);
      }
    } else {
      userOpt = users.findByUsername(id);
      if (!userOpt.isPresent()) {
        userOpt = users.findByEmail(id);
      }
    }

    return createTokenInternal(userOpt);
  }

  private Optional<PasswordResetToken> createTokenInternal(Optional<User> userOpt) {
    if (userOpt.isEmpty()) {
      log.info("No reset token created (identifier not found).");
      System.out.println("[DEV] No reset token created (identifier not found).");
      return Optional.empty();
    }

    var token = new PasswordResetToken();
    token.setUserId(userOpt.get().getId());
    token.setToken(generateToken());
    token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
    token.setUsed(Boolean.FALSE);
    token.setCreatedAt(LocalDateTime.now());

    var saved = tokens.save(token);

    String link = "/reset?token=" + saved.getToken();
    log.info("Password reset link (dev): {}", link);
    System.out.println("[DEV] Password reset link: " + link);

    return Optional.of(saved);
  }

  /**
   * Validates token and returns the associated user if valid.
   */
  public Optional<User> validateToken(String tokenValue) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return Optional.empty();

    var t = tokOpt.get();
    if (Boolean.TRUE.equals(t.getUsed())) return Optional.empty();
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return Optional.empty();

    return users.findById(t.getUserId());
  }

  /**
   * Resets the password if the token is valid and unused.
   */
  public boolean resetPassword(String tokenValue, String rawNewPassword) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return false;

    var t = tokOpt.get();
    if (Boolean.TRUE.equals(t.getUsed())) return false;
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return false;

    var userOpt = users.findById(t.getUserId());
    if (userOpt.isEmpty()) return false;

    var user = userOpt.get();
    user.setPassword(encoder.encode(rawNewPassword));
    users.save(user);

    t.setUsed(Boolean.TRUE);
    tokens.save(t);

    log.info("Password reset completed for user id {}", user.getId());
    System.out.println("[DEV] Password reset completed for user id " + user.getId());

    return true;
  }

  private String generateToken() {
    byte[] buf = new byte[24]; // 48 hex chars
    random.nextBytes(buf);
    return hex.formatHex(buf);
  }

  private String safe(String s) {
    return s == null ? "" : s.trim();
  }
}
