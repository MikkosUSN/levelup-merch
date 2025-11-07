package com.clc.levelup.dto;

import javax.validation.constraints.NotBlank;

/**
 * Represents login form data submitted by the user.
 * Contains either an email or username and the corresponding password.
 * Used by {@link com.clc.levelup.controllers.LoginController} during authentication.
 */
public class LoginRequest {

  /** User’s email or username field. Must not be blank. */
  @NotBlank(message = "Please enter your email or username.")
  private String emailOrUsername;

  /** User’s password field. Must not be blank. */
  @NotBlank(message = "Please enter your password.")
  private String password;

  /**
   * Get the email or username entered by the user.
   * @return email or username string
   */
  public String getEmailOrUsername() {
    return emailOrUsername;
  }

  /**
   * Set the email or username field.
   * @param emailOrUsername email or username input
   */
  public void setEmailOrUsername(String emailOrUsername) {
    this.emailOrUsername = emailOrUsername;
  }

  /**
   * Get the entered password.
   * @return password string
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the password field.
   * @param password password input
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
