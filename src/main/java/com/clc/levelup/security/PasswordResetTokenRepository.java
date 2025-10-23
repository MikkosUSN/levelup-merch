package com.clc.levelup.security;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/*
 * Access to password reset tokens.
 */
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByToken(String token);
}
