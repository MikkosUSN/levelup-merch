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
   * Exposes the logged-in username (from session "principal") as "currentUser".
   */
  @ModelAttribute("currentUser")
  public String currentUser(HttpSession session) {
    Object p = (session == null) ? null : session.getAttribute("principal");
    return (p == null) ? null : p.toString();
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
    return session != null && session.getAttribute("principal") != null;
  }
}
