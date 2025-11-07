package com.clc.levelup.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clc.levelup.dto.LoginRequest;
import com.clc.levelup.model.User;
import com.clc.levelup.service.AuthService;

/**
 * Handles the login form workflow.
 * On success, stores a simple username in the HTTP session under both
 * "principal" (legacy) and "currentUser" (used by templates).
 */
@Controller
public class LoginController {

    private final AuthService auth;

    /**
     * Create a LoginController with an AuthService dependency.
     * @param auth authentication service used to verify credentials
     */
    public LoginController(AuthService auth) {
        this.auth = auth;
    }

    /**
     * Display the login page. Accepts an optional info message, e.g., after logout or registration.
     * @param model MVC model
     * @param message optional informational message to display on the page
     * @return login view name
     */
    @GetMapping("/login")
    public String showLogin(Model model,
                            @RequestParam(value = "message", required = false) String message) {
        // Ensure a backing object exists for the form
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }

        // Pass through any optional message
        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message);
        }

        return "auth/login";
    }

    /**
     * Process the submitted login form.
     * Validates fields, authenticates via AuthService, and on success stores a friendly username in session.
     * @param form validated login request DTO
     * @param result binding/validation result
     * @param session active HTTP session
     * @param model MVC model
     * @return redirect to /products on success, or redisplay login on error
     */
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest form,
                               BindingResult result,
                               HttpSession session,
                               Model model) {

        // Return to the form if field-level validation failed
        if (result.hasErrors()) {
            return "auth/login";
        }

        // Authenticate against the in-memory AuthService
        if (!auth.authenticate(form)) {
            // Global error key (messages.properties can map "auth.invalid" to a friendly message)
            result.reject("auth.invalid");
            // Friendly inline message for the view
            model.addAttribute("message", "Invalid credentials, please try again.");
            return "auth/login";
        }

        // Success: resolve a display-friendly principal (username string)
        Object principal = auth.toPrincipal(form.getEmailOrUsername());
        String displayName = (principal instanceof User)
                ? ((User) principal).getUsername()
                : String.valueOf(principal);

        // Keep both keys: "principal" (legacy) and "currentUser" (used by GlobalModelAttributes / templates)
        session.setAttribute("principal", displayName);
        session.setAttribute("currentUser", displayName);

        // Send the user to the catalog after a successful login
        return "redirect:/products";
    }
}
