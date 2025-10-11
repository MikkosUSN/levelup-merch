package com.clc.levelup.config;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds common model attributes to all MVC views.
 */
@ControllerAdvice
public class GlobalModelAttributes {

  /**
   * Exposes the logged-in username (from session "principal"/"currentUser") as "currentUser".
   * Team note (M4): Prefer "currentUser" but also accept legacy "principal".
   */
  @ModelAttribute("currentUser")
  public String currentUser(HttpSession session) {
    if (session == null) return null;
    Object val = session.getAttribute("currentUser");
    if (val == null) val = session.getAttribute("principal"); // compatibility
    return (val == null) ? null : String.valueOf(val);
  }

  /**
   * Compatibility attribute for templates that expect "loggedInUser".
   * Uses the same session value as currentUser.
   */
  @ModelAttribute("loggedInUser")
  public String loggedInUser(HttpSession session) {
    return currentUser(session);
  }

  /**
   * Boolean flag to toggle UI elements based on login state.
   */
  @ModelAttribute("isLoggedIn")
  public boolean isLoggedIn(HttpSession session) {
    return currentUser(session) != null;
  }
}
