package com.clc.levelup.controllers;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.service.UserService;

/**
 * Handles registration form display and submission.
 * Performs validation checks and creates a new user record
 * by delegating persistence logic to UserService.
 */
@Controller
public class RegisterController {

    private final UserService users;

    /**
     * Create a new RegisterController instance.
     * @param users service responsible for managing user accounts
     */
    public RegisterController(UserService users) {
        this.users = users;
    }

    /**
     * Display the registration form and optional message (for redirects or info prompts).
     * @param model MVC model for view attributes
     * @param message optional text message to display on the form
     * @return registration form view
     */
    @GetMapping("/register")
    public String showForm(Model model,
                           @RequestParam(value = "message", required = false) String message) {
        // Initialize the form backing object if not present
        if (!model.containsAttribute("userRegistration")) {
            model.addAttribute("userRegistration", new UserRegistration());
        }

        // Pass through optional info message
        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message);
        }

        return "auth/register";
    }

    /**
     * Process submitted registration form.
     * Performs uniqueness checks for email and username before creating the account.
     * @param dto form data mapped to UserRegistration DTO
     * @param br validation result container
     * @param model MVC model
     * @return success page or redisplay form with errors
     */
    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("userRegistration") UserRegistration dto,
                              BindingResult br,
                              Model model) {

        // Normalize inputs for consistent uniqueness checks
        final String normalizedEmail = dto.getEmail() == null ? "" : dto.getEmail().trim().toLowerCase();
        final String normalizedUsername = dto.getUsername() == null ? "" : dto.getUsername().trim();

        // Check if email or username already exists (case-insensitive)
        if (users.existsByEmail(normalizedEmail) || users.existsByEmailIgnoreCase(normalizedEmail)) {
            br.rejectValue("email", "user.email.exists");
        }
        if (users.existsByUsername(normalizedUsername) || users.existsByUsernameIgnoreCase(normalizedUsername)) {
            br.rejectValue("username", "user.username.exists");
        }

        // Redisplay form if any validation errors occur
        if (br.hasErrors()) {
            return "auth/register";
        }

        // Save user record (password handling managed by service)
        users.emulateCreate(dto); // Keeps consistent with local testing

        // Optional: replace with production method that applies password hashing and default roles
        // users.create(dto);

        // Success message shown on confirmation page
        model.addAttribute("message", "Registration successful for " + normalizedUsername + "!");
        return "auth/register-success";
    }
}
