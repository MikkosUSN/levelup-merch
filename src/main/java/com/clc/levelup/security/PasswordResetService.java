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

  /** Existing for compatibility when called by email only. */
  public Optional<PasswordResetToken> createTokenForEmail(String email) {
    return createTokenInternal(users.findByEmail(safe(email)));
  }

  // Update (M6): new helper that accepts either email or username
  public Optional<PasswordResetToken> createTokenForIdentifier(String identifier) {
    String id = safe(identifier);
    Optional<User> userOpt;

    // Try email first if it looks like one, else try username
    if (id.contains("@")) {
      userOpt = users.findByEmail(id);
      if (!userOpt.isPresent()) {
        // fallback to username in case they typed user@name accidentally stored as username
        userOpt = users.findByUsername(id);
      }
    } else {
      userOpt = users.findByUsername(id);
      if (!userOpt.isPresent()) {
        // fallback to email if they typed their email without @ by mistake
        userOpt = users.findByEmail(id);
      }
    }
    return createTokenInternal(userOpt);
  }

  private Optional<PasswordResetToken> createTokenInternal(Optional<User> userOpt) {
    if (userOpt.isEmpty()) return Optional.empty();
    var token = new PasswordResetToken();
    token.setUserId(userOpt.get().getId());
    token.setToken(generateToken());
    token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
    // Update (M6): map to Boolean to match JDBC for TINYINT(1)
    token.setUsed(Boolean.FALSE);
    // Update (M6): set created_at for housekeeping/inspection
    token.setCreatedAt(LocalDateTime.now());
    return Optional.of(tokens.save(token));
  }

  public Optional<User> validateToken(String tokenValue) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return Optional.empty();
    var t = tokOpt.get();
    // Update (M6): Boolean-safe checks
    if (Boolean.TRUE.equals(t.getUsed())) return Optional.empty();
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return Optional.empty();
    return users.findById(t.getUserId());
  }

  public boolean resetPassword(String tokenValue, String rawNewPassword) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return false;
    var t = tokOpt.get();
    // Update (M6): Boolean-safe checks
    if (Boolean.TRUE.equals(t.getUsed())) return false;
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return false;

    var userOpt = users.findById(t.getUserId());
    if (userOpt.isEmpty()) return false;

    var user = userOpt.get();
    user.setPassword(encoder.encode(rawNewPassword));
    users.save(user);

    // Update (M6): mark token consumed using Boolean
    t.setUsed(Boolean.TRUE);
    tokens.save(t);
    return true;
  }

  private String generateToken() {
    byte[] buf = new byte[24]; // 48 hex chars
    random.nextBytes(buf);
    return hex.formatHex(buf);
  }

  private String safe(String s) { return s == null ? "" : s.trim(); }
}
