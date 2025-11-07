package com.clc.levelup.config;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the application with a demo user account on startup.
 * This allows the team and instructor to log in immediately without registration.
 * Safe to remove once persistent users are stored in a production database.
 */
@Configuration
public class DataSeed {

  /**
   * Create a demo user if none exists.
   * Runs automatically when the application starts.
   * @param users UserService used to check and create the demo user
   * @return ApplicationRunner that performs the seed logic
   */
  @Bean
  ApplicationRunner seed(UserService users) {
    return args -> {
      // Prevent duplicate creation if the demo account already exists
      if (!users.existsByEmail("demo@clc.com")) {
        // Create a demo user profile for quick login
        UserRegistration d = new UserRegistration();
        d.setFirstName("Demo");
        d.setLastName("User");
        d.setEmail("demo@clc.com");
        d.setPhone("706-888-3014");
        d.setUsername("demo");
        d.setPassword("pass1234");
        d.setConfirmPassword("pass1234");

        // Use service helper to emulate account creation (non-persistent for now)
        users.emulateCreate(d);
      }
    };
  }
}
