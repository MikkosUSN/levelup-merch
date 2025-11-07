package com.clc.levelup.config;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provides shared model attributes available across all MVC views.
 * Enables Thymeleaf templates to access user-related data
 * such as the current username and login status.
 */
@ControllerAdvice
public class GlobalModelAttributes {

  /**
   * Expose the logged-in username under the model name "currentUser".
   * Looks for the value in session attributes "currentUser" or "principal"
   * for backward compatibility with earlier session usage.
   * @param session active HTTP session
   * @return username as String, or null if not logged in
   */
  @ModelAttribute("currentUser")
  public String currentUser(HttpSession session) {
    if (session == null) return null;

    // Try both modern and legacy session attributes
    Object val = session.getAttribute("currentUser");
    if (val == null) {
      val = session.getAttribute("principal");
    }

    return (val == null) ? null : String.valueOf(val);
  }

  /**
   * Expose "loggedInUser" as an alternate key for templates that still reference it.
   * Calls currentUser() to maintain a single source of truth.
   * @param session active HTTP session
   * @return username or null if not logged in
   */
  @ModelAttribute("loggedInUser")
  public String loggedInUser(HttpSession session) {
    return currentUser(session);
  }

  /**
   * Boolean flag used by templates to show or hide login-related links.
   * Returns true when a user is logged in (session contains a username).
   * @param session active HTTP session
   * @return true if user is logged in; false otherwise
   */
  @ModelAttribute("isLoggedIn")
  public boolean isLoggedIn(HttpSession session) {
    // Helps Thymeleaf conditionally render navigation buttons or profile links
    return currentUser(session) != null;
  }
}
