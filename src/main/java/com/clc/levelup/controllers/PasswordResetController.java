package com.clc.levelup.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;               // Update (M6): dev toggle
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

  // Update (M6): dev-only option to show the link on the page (not just console)
  @Value("${app.showResetLinkInUi:false}")
  private boolean showResetLinkInUi;

  public PasswordResetController(PasswordResetService resetService) {
    this.resetService = resetService;
  }

  @GetMapping("/forgot")
  public String showForgotForm() {
    return "auth/forgot";
  }

  // Update (M6): accept identifier (email or username)
  @PostMapping("/forgot")
  public String handleForgot(@RequestParam("identifier") String identifier, Model model) {
    var tokenOpt = resetService.createTokenForIdentifier(identifier);

    // Always generic (no account enumeration)
    model.addAttribute("message", "If the email exists, a reset link has been created.");

    // Log token for local testing
    tokenOpt.ifPresent(tok -> log.info("Password reset link (dev): /reset?token={}", tok.getToken()));

    // Optional: in dev, also show the link directly on the page
    if (showResetLinkInUi && tokenOpt.isPresent()) {
      model.addAttribute("devLink", "/reset?token=" + tokenOpt.get().getToken());
    }

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
