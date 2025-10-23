package com.clc.levelup.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.clc.levelup.security.PasswordResetService;

/*
 * Handles /forgot and /reset routes.
 * Logs the reset URL to console for local testing.
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
    return "auth/forgot"; // simple form with email field
  }

  @PostMapping("/forgot")
  public String handleForgot(@RequestParam("email") String email, Model model) {
    var tokenOpt = resetService.createTokenForEmail(email);
    if (tokenOpt.isEmpty()) {
      model.addAttribute("message", "If the email exists, a reset link has been created.");
      return "auth/forgot-done";
    }

    var token = tokenOpt.get().getToken();
    var link = "/reset?token=" + token; // local dev: relative path
    log.info("Password reset link (dev): {}", link);

    model.addAttribute("message", "A reset link has been generated (see server console).");
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
    return "auth/reset"; // form with password + confirm + hidden token
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
