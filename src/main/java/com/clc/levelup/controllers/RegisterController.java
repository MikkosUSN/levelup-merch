package com.clc.levelup.controllers;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// allow simple info message in the view (optional)
import org.springframework.web.bind.annotation.RequestParam;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.service.UserService;

/**
 * Registration flow (no DB yet).
 * Uses in-memory UserService to emulate create and checks duplicates.
 */
@Controller
public class RegisterController {

    private final UserService users;

    public RegisterController(UserService users) {
        this.users = users;
    }

    @GetMapping("/register")
    public String showForm(Model model,
                           // optional message support for simple notices
                           @RequestParam(value = "message", required = false) String message) {
        // Backing object for the form (fields must match DTO)
        if (!model.containsAttribute("userRegistration")) {
            model.addAttribute("userRegistration", new UserRegistration());
        }

        // pass through optional info message (e.g., “Please complete all fields”)
        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message); // simple, user-friendly note
        }

        // templates/auth/register.html
        return "auth/register";
    }

    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("userRegistration") UserRegistration dto,
                              BindingResult br,
                              Model model) {

        // Duplicate checks use message keys (messages.properties)
        if (users.existsByEmail(dto.getEmail())) {
            br.rejectValue("email", "user.email.exists");
        }
        if (users.existsByUsername(dto.getUsername())) {
            br.rejectValue("username", "user.username.exists");
        }

        if (br.hasErrors()) {
            return "auth/register";
        }

        // Persist in memory for demo
        users.emulateCreate(dto);

        // add a simple message the template can show
        model.addAttribute("message", "Registration successful for " + dto.getUsername() + "!");

        // Show a simple success page
        // templates/auth/register-success.html
        return "auth/register-success";

        // Note: if later you prefer to send the user to login with a message:
        // return "redirect:/login?message=" + UriUtils.encode("Registration successful. Please log in.", StandardCharsets.UTF_8);
    }
}
