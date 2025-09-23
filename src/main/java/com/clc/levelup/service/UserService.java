package com.clc.levelup.service;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.model.User;
import java.util.Optional;

// Registration-related operations for M2 (in memory).
public interface UserService {
  User emulateCreate(UserRegistration dto);       // create user in memory
  boolean existsByEmail(String email);            // for "email taken" message
  boolean existsByUsername(String username);      // for "username taken" message
  Optional<User> findByEmailOrUsername(String key); // used by login
}
