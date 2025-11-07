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

/**
 * Issues and validates password reset tokens and updates user passwords.
 * <p>
 * Workflow:
 * <ol>
 *   <li>Create a token for a known user (email or username accepted).</li>
 *   <li>Validate the token when the reset link is opened.</li>
 *   <li>Update the user's password (BCrypt via {@link PasswordEncoder}) and mark the token as used.</li>
 * </ol>
 * For local testing, the generated reset link is logged to the console.
 */
@Service
public class PasswordResetService {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

  private final UserRepository users;
  private final PasswordResetTokenRepository tokens;
  private final PasswordEncoder encoder;

  // Random token generator and hex formatter
  private final SecureRandom random = new SecureRandom();
  private final HexFormat hex = HexFormat.of();

  /**
   * Construct the service with required dependencies.
   * @param users user repository
   * @param tokens token repository
   * @param encoder password encoder (BCrypt)
   */
  public PasswordResetService(UserRepository users,
                              PasswordResetTokenRepository tokens,
                              PasswordEncoder encoder) {
    this.users = users;
    this.tokens = tokens;
    this.encoder = encoder;
  }

  /**
   * Create a token for a specific email address.
   * Kept for compatibility with code that passes only email.
   * @param email user email
   * @return created token if user exists
   */
  public Optional<PasswordResetToken> createTokenForEmail(String email) {
    return createTokenInternal(users.findByEmail(safe(email)));
  }

  /**
   * Create a reset token for either an email or a username.
   * @param identifier email or username
   * @return created token if a matching user exists
   */
  public Optional<PasswordResetToken> createTokenForIdentifier(String identifier) {
    String id = safe(identifier);
    Optional<User> userOpt;

    // Try email first if it looks like one; fall back to username
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

  /**
   * Validate a token and return the associated user when valid.
   * @param tokenValue token string
   * @return the user if the token is valid and not expired/used
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
   * Reset the user's password if the token is valid and unused.
   * Marks the token as used after a successful change.
   * @param tokenValue token string
   * @param rawNewPassword new password in plain text
   * @return true if the reset succeeded; false otherwise
   */
  public boolean resetPassword(String tokenValue, String rawNewPassword) {
    var tokOpt = tokens.findByToken(tokenValue);
    if (tokOpt.isEmpty()) return false;

    var t = tokOpt.get();
    if (Boolean.TRUE.equals(t.getUsed())) return false;
    if (t.getExpiresAt().isBefore(LocalDateTime.now())) return false;

    var userOpt = users.findById(t.getUserId());
    if (userOpt.isEmpty()) return false;

    // Hash and save the updated password
    var user = userOpt.get();
    user.setPassword(encoder.encode(rawNewPassword));
    users.save(user);

    // Mark token as used to prevent reuse
    t.setUsed(Boolean.TRUE);
    tokens.save(t);

    log.info("Password reset completed for user id {}", user.getId());
    System.out.println("[DEV] Password reset completed for user id " + user.getId());

    return true;
  }

  // ----- Internals -----

  /**
   * Create and persist a token for the provided user, if present.
   * Also logs the reset URL for local testing.
   */
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

    // Local dev convenience: print the link
    String link = "/reset?token=" + saved.getToken();
    log.info("Password reset link (dev): {}", link);
    System.out.println("[DEV] Password reset link: " + link);

    return Optional.of(saved);
  }

  /** Generate a random 48-character hex token. */
  private String generateToken() {
    byte[] buf = new byte[24]; // 24 bytes -> 48 hex chars
    random.nextBytes(buf);
    return hex.formatHex(buf);
  }

  /** Return a non-null, trimmed string. */
  private String safe(String s) {
    return s == null ? "" : s.trim();
  }
}
