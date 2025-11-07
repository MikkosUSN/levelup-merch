package com.clc.levelup.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.clc.levelup.security.PasswordResetService;

/**
 * Handles password reset requests:
 * - /forgot: request a reset token
 * - /reset : validate token and set a new password
 * For local testing, the generated reset URL is logged and shown on the page.
 */
@Controller
public class PasswordResetController {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);
  private final PasswordResetService resetService;

  /**
   * Create a controller with a PasswordResetService dependency.
   * @param resetService service that issues and validates password reset tokens
   */
  public PasswordResetController(PasswordResetService resetService) {
    this.resetService = resetService;
  }

  /**
   * Display the "forgot password" form.
   * @return forgot view
   */
  @GetMapping("/forgot")
  public String showForgotForm() {
    return "auth/forgot";
  }

  /**
   * Handle the submission of an identifier (email or username) and create a reset token if possible.
   * Uses a neutral confirmation message to avoid account enumeration.
   * Also logs and surfaces a dev-only link for quick local testing.
   * @param identifier email or username
   * @param model MVC model
   * @return confirmation page
   */
  @PostMapping("/forgot")
  public String handleForgot(@RequestParam("identifier") String identifier, Model model) {
    var tokenOpt = resetService.createTokenForIdentifier(identifier);

    // Neutral message regardless of account existence to prevent enumeration
    model.addAttribute("message", "If the email exists, a reset link has been created.");

    // Log and display a dev link for convenience during local testing
    tokenOpt.ifPresentOrElse(tok -> {
      String link = "/reset?token=" + tok.getToken();
      log.info("Password reset link (dev): {}", link); // SLF4J log
      System.out.println("[DEV] Password reset link: " + link); // Console for simple setups

      // Provide quick-access token/link on the confirmation page (dev-only usage)
      model.addAttribute("devLink", link);
      model.addAttribute("token", tok.getToken());
    }, () -> {
      log.info("No reset token created. Identifier did not match an account.");
      System.out.println("[DEV] No reset token created (identifier not found).");
    });

    return "auth/forgot-done";
  }

  /**
   * Display the reset password form after token validation.
   * Shows an error page if the token is invalid or expired.
   * @param token reset token
   * @param model MVC model
   * @return reset form or error page
   */
  @GetMapping("/reset")
  public String showResetForm(@RequestParam("token") String token, Model model) {
    var userOpt = resetService.validateToken(token);
    if (userOpt.isEmpty()) {
      model.addAttribute("error", "Invalid or expired token.");
      return "auth/reset-error";
    }
    model.addAttribute("token", token);
    return "auth/reset";
  }

  /**
   * Process the reset form and update the user's password when the token is valid.
   * Ensures basic checks: non-empty password and a matching confirmation.
   * @param token reset token
   * @param password new password
   * @param confirm confirmation of new password
   * @param model MVC model
   * @return done page on success; redisplay reset or error page otherwise
   */
  @PostMapping("/reset")
  public String handleReset(@RequestParam("token") String token,
                            @RequestParam("password") String password,
                            @RequestParam("confirm") String confirm,
                            Model model) {
    // Basic form validation
    if (password == null || password.isBlank() || !password.equals(confirm)) {
      model.addAttribute("token", token);
      model.addAttribute("error", "Passwords must match and not be blank.");
      return "auth/reset";
    }

    // Attempt password reset; fail for invalid/expired token
    boolean ok = resetService.resetPassword(token, password);
    if (!ok) {
      model.addAttribute("error", "Invalid or expired token.");
      return "auth/reset-error";
    }

    // Success
    model.addAttribute("message", "Password updated. You can log in now.");
    return "auth/reset-done";
  }
}
