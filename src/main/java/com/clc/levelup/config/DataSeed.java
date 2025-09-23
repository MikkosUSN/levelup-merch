package com.clc.levelup.config;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Adds a demo user on startup so the team can log in immediately.
@Configuration
public class DataSeed {

  @Bean
  ApplicationRunner seed(UserService users) {
    return args -> {
      if (!users.existsByEmail("demo@clc.com")) {
        UserRegistration d = new UserRegistration();
        d.setFirstName("Demo");
        d.setLastName("User");
        d.setEmail("demo@clc.com");
        d.setPhone("706-888-3014");
        d.setUsername("demo");
        d.setPassword("pass1234");
        d.setConfirmPassword("pass1234");
        users.emulateCreate(d);
      }
    };
  }
}
