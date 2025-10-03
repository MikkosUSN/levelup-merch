package com.clc.levelup.dto;

import javax.validation.constraints.*;
import com.clc.levelup.validation.PasswordMatches;

// Data from the Register form. Validations keep input clean and helpful.
@PasswordMatches(password = "password", confirmPassword = "confirmPassword")
public class UserRegistration {

  @NotBlank @Size(min=2, max=50) private String firstName;  // 2–50 chars
  @NotBlank @Size(min=2, max=50) private String lastName;   // 2–50 chars
  @NotBlank @Email               private String email;      // standard email

  // 10 digits, punctuation allowed: 706-888-3014, (706)8883014, 7068883014
  @NotBlank
  @Pattern(regexp="^\\D?\\d{3}\\D?\\d{3}\\D?\\d{4}$")
  private String phone;

  // Letters/numbers only, 4–20 chars
  @NotBlank
  @Pattern(regexp="^[A-Za-z0-9]{4,20}$")
  private String username;

  // At least 6 characters
  @NotBlank @Size(min=6) private String password;

  // Same minimum as password
  @NotBlank @Size(min=6) private String confirmPassword;

  // Getters/setters so form binding works later
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
