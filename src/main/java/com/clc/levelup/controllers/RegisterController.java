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

/*
 * Handles registration form display and submission.
 * Performs validation and creates a new user record.
 */
@Controller
public class RegisterController {

    private final UserService users;

    public RegisterController(UserService users) {
        this.users = users;
    }

    @GetMapping("/register")
    public String showForm(Model model,
                           @RequestParam(value = "message", required = false) String message) {
        if (!model.containsAttribute("userRegistration")) {
            model.addAttribute("userRegistration", new UserRegistration());
        }

        if (message != null && !message.isBlank()) {
            model.addAttribute("message", message);
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute("userRegistration") UserRegistration dto,
                              BindingResult br,
                              Model model) {

        if (users.existsByEmail(dto.getEmail())) {
            br.rejectValue("email", "user.email.exists");
        }
        if (users.existsByUsername(dto.getUsername())) {
            br.rejectValue("username", "user.username.exists");
        }

        if (br.hasErrors()) {
            return "auth/register";
        }

        // Store user data (password hashing handled in service)
        users.emulateCreate(dto);

        model.addAttribute("message", "Registration successful for " + dto.getUsername() + "!");
        return "auth/register-success";
    }
}
