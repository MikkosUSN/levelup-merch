package com.clc.levelup.dto;

import javax.validation.constraints.*;
import com.clc.levelup.validation.PasswordMatches;

/**
 * Represents user data submitted through the registration form.
 * Provides field-level validation for clean, user-friendly input.
 * The {@link PasswordMatches} annotation ensures both password fields match.
 */
@PasswordMatches(password = "password", confirmPassword = "confirmPassword")
public class UserRegistration {

  /** User’s first name (2–50 characters). */
  @NotBlank(message = "First name is required.")
  @Size(min = 2, max = 50, message = "First name must be 2–50 characters.")
  private String firstName;

  /** User’s last name (2–50 characters). */
  @NotBlank(message = "Last name is required.")
  @Size(min = 2, max = 50, message = "Last name must be 2–50 characters.")
  private String lastName;

  /** Valid email address, required for account creation. */
  @NotBlank(message = "Email is required.")
  @Email(message = "Please enter a valid email address.")
  private String email;

  /**
   * Optional phone number.
   * If entered, must match a 10-digit pattern allowing punctuation (e.g., 706-888-3014 or (706)8883014).
   */
  @Pattern(
      regexp = "^$|^\\D?\\d{3}\\D?\\d{3}\\D?\\d{4}$",
      message = "Enter a 10-digit phone number (dashes or parentheses allowed), or leave blank."
  )
  private String phone;

  /** Username containing letters, numbers, and ., _, or -; 4–20 characters. */
  @NotBlank(message = "Username is required.")
  @Pattern(
      regexp = "^[A-Za-z0-9._-]{4,20}$",
      message = "Username must be 4–20 characters: letters, numbers, dot (.), underscore (_), or dash (-)."
  )
  private String username;

  /**
   * Password field.
   * Must be at least six characters, containing at least one letter and one number.
   */
  @NotBlank(message = "Password is required.")
  @Size(min = 6, max = 128, message = "Password must be at least 6 characters.")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$",
      message = "Password must contain at least one letter and one number."
  )
  private String password;

  /**
   * Confirmation password field.
   * Must meet the same minimum length and match the password.
   */
  @NotBlank(message = "Confirm password is required.")
  @Size(min = 6, max = 128, message = "Confirm password must be at least 6 characters.")
  private String confirmPassword;

  // ----- Getters and Setters -----

  /**
   * Get the user's first name.
   * @return first name
   */
  public String getFirstName() { return firstName; }

  /**
   * Set the user's first name.
   * @param firstName value to assign
   */
  public void setFirstName(String firstName) { this.firstName = firstName; }

  /**
   * Get the user's last name.
   * @return last name
   */
  public String getLastName() { return lastName; }

  /**
   * Set the user's last name.
   * @param lastName value to assign
   */
  public void setLastName(String lastName) { this.lastName = lastName; }

  /**
   * Get the user's email address.
   * @return email address
   */
  public String getEmail() { return email; }

  /**
   * Set the user's email address.
   * @param email value to assign
   */
  public void setEmail(String email) { this.email = email; }

  /**
   * Get the user's phone number.
   * @return phone number, may be blank
   */
  public String getPhone() { return phone; }

  /**
   * Set the user's phone number.
   * @param phone value to assign
   */
  public void setPhone(String phone) { this.phone = phone; }

  /**
   * Get the chosen username.
   * @return username
   */
  public String getUsername() { return username; }

  /**
   * Set the chosen username.
   * @param username value to assign
   */
  public void setUsername(String username) { this.username = username; }

  /**
   * Get the password string.
   * @return password
   */
  public String getPassword() { return password; }

  /**
   * Set the password string.
   * @param password value to assign
   */
  public void setPassword(String password) { this.password = password; }

  /**
   * Get the confirmation password.
   * @return confirm password
   */
  public String getConfirmPassword() { return confirmPassword; }

  /**
   * Set the confirmation password.
   * @param confirmPassword value to assign
   */
  public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
