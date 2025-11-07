package com.clc.levelup.security;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing {@link PasswordResetToken} entities.
 * Provides lookups for active tokens and cleanup of expired ones.
 */
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

  /**
   * Find a reset token by its unique token string.
   * Used during validation and password reset.
   * @param token token string value
   * @return optional containing the token if found
   */
  Optional<PasswordResetToken> findByToken(String token);

  /**
   * Delete all tokens that expired before the specified cutoff time.
   * Useful for scheduled maintenance and cleanup tasks.
   * @param cutoffTime time before which tokens should be deleted
   */
  void deleteByExpiresAtBefore(LocalDateTime cutoffTime);

  /**
   * Retrieve the most recent token created for a given user.
   * Used to prevent reuse or creation of overlapping tokens.
   * @param userId ID of the user
   * @return optional containing the most recent token for the user
   */
  Optional<PasswordResetToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
