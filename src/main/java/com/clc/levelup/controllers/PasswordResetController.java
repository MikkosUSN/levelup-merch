package com.clc.levelup.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.clc.levelup.security.PasswordResetService;

/*
 * Handles /forgot and /reset routes.
 * Logs the reset URL for local testing.
 */
@Controller
public class PasswordResetController {

  private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);
  private final PasswordResetService resetService;

  public PasswordResetController(PasswordResetService resetService) {
    this.resetService = resetService;
  }

  @GetMapping("/forgot")
  public String showForgotForm() {
    return "auth/forgot";
  }

  @PostMapping("/forgot")
  public String handleForgot(@RequestParam("identifier") String identifier, Model model) {
    var tokenOpt = resetService.createTokenForIdentifier(identifier);

    // Generic message (no account enumeration)
    model.addAttribute("message", "If the email exists, a reset link has been created.");

    // Always log what happened so it's obvious in the console
    tokenOpt.ifPresentOrElse(tok -> {
      String link = "/reset?token=" + tok.getToken();
      // Log via SLF4J
      log.info("Password reset link (dev): {}", link);
      // Also print to stdout so it appears in any console setup
      System.out.println("[DEV] Password reset link: " + link);

      // Put link + token on page for fast testing
      model.addAttribute("devLink", link);
      model.addAttribute("token", tok.getToken());
    }, () -> {
      log.info("No reset token created. Identifier did not match an account.");
      System.out.println("[DEV] No reset token created (identifier not found).");
    });

    return "auth/forgot-done";
  }

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

  @PostMapping("/reset")
  public String handleReset(@RequestParam("token") String token,
                            @RequestParam("password") String password,
                            @RequestParam("confirm") String confirm,
                            Model model) {
    if (password == null || password.isBlank() || !password.equals(confirm)) {
      model.addAttribute("token", token);
      model.addAttribute("error", "Passwords must match and not be blank.");
      return "auth/reset";
    }
    boolean ok = resetService.resetPassword(token, password);
    if (!ok) {
      model.addAttribute("error", "Invalid or expired token.");
      return "auth/reset-error";
    }
    model.addAttribute("message", "Password updated. You can log in now.");
    return "auth/reset-done";
  }
}
