package com.clc.levelup.dto;

import javax.validation.constraints.NotBlank;

// Login form data: either email or username + password.
public class LoginRequest {

  @NotBlank // required
  private String emailOrUsername;

  @NotBlank // required
  private String password;

  public String getEmailOrUsername() { return emailOrUsername; }
  public void setEmailOrUsername(String emailOrUsername) { this.emailOrUsername = emailOrUsername; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}
