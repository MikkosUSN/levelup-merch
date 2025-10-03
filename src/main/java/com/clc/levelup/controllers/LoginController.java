package com.clc.levelup.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.clc.levelup.dto.LoginRequest;
import com.clc.levelup.service.AuthService;

/**
 * Login flow for M2 (no DB/security yet).
 * On success, stores a simple principal in the HTTP session under key "principal".
 */
@Controller
public class LoginController {

    private final AuthService auth;

    public LoginController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        // Backing object for the form (fields: emailOrUsername, password)
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest form,
                               BindingResult result,
                               HttpSession session) {
        // Field-level validation errors
        if (result.hasErrors()) {
            return "auth/login";
        }

        // Use in-memory AuthService for M2
        if (!auth.authenticate(form)) {
            // Global error uses messages.properties key
            result.reject("auth.invalid");
            return "auth/login";
        }

        // Success: stash a simple principal in session for navbar/menu state
        session.setAttribute("principal", auth.toPrincipal(form.getEmailOrUsername()));
        return "redirect:/products";
    }
}
