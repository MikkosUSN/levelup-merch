package com.clc.levelup.config;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds shared model attributes to all MVC views.
 * Used by Thymeleaf templates to show user-specific elements in the UI.
 */
@ControllerAdvice
public class GlobalModelAttributes {

  /**
   * Exposes the logged-in username (from session "principal" or "currentUser") as "currentUser".
   * This supports both old and new attribute names for compatibility.
   */
  @ModelAttribute("currentUser")
  public String currentUser(HttpSession session) {
    if (session == null) return null;
    Object val = session.getAttribute("currentUser");
    if (val == null) val = session.getAttribute("principal"); // compatibility with older session usage
    return (val == null) ? null : String.valueOf(val);
  }

  /**
   * Provides backward compatibility for templates expecting "loggedInUser".
   * Uses the same session value as currentUser.
   */
  @ModelAttribute("loggedInUser")
  public String loggedInUser(HttpSession session) {
    return currentUser(session);
  }

  /**
   * Boolean flag that returns true when a user is logged in.
   * Allows conditional rendering in templates (e.g., Login vs Logout).
   */
  @ModelAttribute("isLoggedIn")
  public boolean isLoggedIn(HttpSession session) {
    return currentUser(session) != null;
  }
}
