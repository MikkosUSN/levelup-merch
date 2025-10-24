package com.clc.levelup.security;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/*
 * Access to password reset tokens.
 */
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

  /** Find a reset token by its unique value. */
  Optional<PasswordResetToken> findByToken(String token);

  // Update (M6): allow cleanup of expired tokens (optional maintenance method)
  void deleteByExpiresAtBefore(java.time.LocalDateTime cutoffTime);

  // Update (M6): optional lookup to prevent reusing same user token (for enhanced safety)
  Optional<PasswordResetToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
