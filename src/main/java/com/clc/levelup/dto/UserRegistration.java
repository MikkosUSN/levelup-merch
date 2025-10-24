package com.clc.levelup.dto;

import javax.validation.constraints.*;
import com.clc.levelup.validation.PasswordMatches;

/*
 * Team: Data from the Register form. Validations keep input clean and helpful.
 * Update: phone is optional on the form, so DTO allows blank phone but validates format if present.
 */
@PasswordMatches(password = "password", confirmPassword = "confirmPassword")
public class UserRegistration {

  @NotBlank(message = "First name is required.")
  @Size(min = 2, max = 50, message = "First name must be 2–50 characters.")
  private String firstName;

  @NotBlank(message = "Last name is required.")
  @Size(min = 2, max = 50, message = "Last name must be 2–50 characters.")
  private String lastName;

  @NotBlank(message = "Email is required.")
  @Email(message = "Please enter a valid email address.")
  private String email;

  // Team: Phone is optional. If provided, must be a 10-digit number (punctuation allowed).
  // Accepts: 706-888-3014, (706)8883014, 7068883014
  @Pattern(regexp = "^$|^\\D?\\d{3}\\D?\\d{3}\\D?\\d{4}$",
           message = "Enter a 10-digit phone number (dashes or parentheses allowed), or leave blank.")
  private String phone;

  // Team: Username letters/numbers plus dot/underscore/dash; 4–20 chars.
  @NotBlank(message = "Username is required.")
  @Pattern(regexp = "^[A-Za-z0-9._-]{4,20}$",
           message = "Username must be 4–20 characters: letters, numbers, dot (.), underscore (_), or dash (-).")
  private String username;

  // Team: At least 6 characters; server message aligned with the form hint.
  @NotBlank(message = "Password is required.")
  @Size(min = 6, max = 128, message = "Password must be at least 6 characters.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$",
           message = "Password must contain at least one letter and one number.")
  private String password;

  // Team: Same minimum as password; equality enforced by @PasswordMatches.
  @NotBlank(message = "Confirm password is required.")
  @Size(min = 6, max = 128, message = "Confirm password must be at least 6 characters.")
  private String confirmPassword;

  // ----- Getters / Setters -----
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getConfirmPassword() { return confirmPassword; }
  public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
