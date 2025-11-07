package com.clc.levelup.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles user logout requests.
 * Invalidates the current session and redirects the user to the home page.
 */
@Controller
public class LogoutController {

    /**
     * Log the user out by invalidating the session.
     * Clears all session attributes (including user data) to ensure a clean logout.
     * @param session active HTTP session
     * @return redirect to the site home page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Double-check that the session exists before invalidating
        if (session != null) {
            session.invalidate();
        }

        // After logout, send user back to the home page
        return "redirect:/";
    }
}
