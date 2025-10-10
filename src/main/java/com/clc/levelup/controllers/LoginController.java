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
import com.clc.levelup.service.AuthService;

/**
 * Login flow (no DB/security yet).
 * On success, stores a simple principal in the HTTP session under key "principal".
 */
@Controller
public class LoginController {

    private final AuthService auth;

    public LoginController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/login")
    public String showLogin(Model model,
                            // simple message support (optional, after logout or registration)
                            @RequestParam(value = "message", required = false) String message) {
        // Backing object for the form (fields: emailOrUsername, password)
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }

        // Pass through optional info message
        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message);
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest form,
                               BindingResult result,
                               HttpSession session,
                               Model model) {

        // Field-level validation errors
        if (result.hasErrors()) {
            return "auth/login";
        }

        // Use in-memory AuthService
        if (!auth.authenticate(form)) {
            // Global error uses messages.properties key if needed
            result.reject("auth.invalid");

            // Removed the "error" attribute as requested
            model.addAttribute("message", "Invalid credentials, please try again.");

            return "auth/login";
        }

        // Success: stash a simple principal in session for navbar/menu state
        session.setAttribute("principal", auth.toPrincipal(form.getEmailOrUsername()));

        // Redirect to products after successful login
        return "redirect:/products";
    }
}
