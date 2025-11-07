package com.clc.levelup.validation;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validation annotation used to ensure two fields within a class match.
 * <p>
 * Typically used on registration forms where a user must confirm their password.
 * The corresponding validator class {@link PasswordMatchesValidator}
 * performs the actual comparison.
 * </p>
 *
 * <pre>
 * Example usage:
 * &#64;PasswordMatches(password = "password", confirmPassword = "confirmPassword")
 * public class UserRegistration { ... }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {

  /**
   * Default error message displayed when the fields do not match.
   * May reference a key in {@code messages.properties}.
   * @return validation message
   */
  String message() default "{validation.passwords.mismatch}";

  /** Validation groups (default: none). */
  Class<?>[] groups() default {};

  /** Payload for additional metadata (default: none). */
  Class<? extends Payload>[] payload() default {};

  /** Name of the first field (usually {@code password}). */
  String password();

  /** Name of the field to compare against (usually {@code confirmPassword}). */
  String confirmPassword();
}
