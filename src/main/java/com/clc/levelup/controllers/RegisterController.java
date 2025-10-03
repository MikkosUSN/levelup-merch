package com.clc.levelup.controllers;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.service.UserService;

/**
 * Registration flow for M2 (no DB yet).
 * Uses in-memory UserService to emulate create and checks duplicates.
 */
@Controller
public class RegisterController {

    private final UserService users;

    public RegisterController(UserService users) {
        this.users = users;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        // Backing object for the form (fields must match DTO)
        model.addAttribute("userRegistration", new UserRegistration());
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

        // Persist in memory for M2 demo
        users.emulateCreate(dto);

        // Show a simple success page
        model.addAttribute("message", "Registration successful for " + dto.getUsername());
        // templates/auth/register-success.html
        return "auth/register-success";
    }
}
