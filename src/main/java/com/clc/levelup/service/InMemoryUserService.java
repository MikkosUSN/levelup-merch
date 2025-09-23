package com.clc.levelup.service;

import com.clc.levelup.dto.UserRegistration;
import com.clc.levelup.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stores users in memory so we can demo registration/login before a real DB.
@Service
public class InMemoryUserService implements UserService {
  private final Map<Long, User> byId = new ConcurrentHashMap<>();     // id -> user
  private final Map<String, Long> byEmail = new ConcurrentHashMap<>(); // email -> id
  private final Map<String, Long> byUser  = new ConcurrentHashMap<>(); // username -> id
  private final AtomicLong seq = new AtomicLong(1);                    // simple id generator

  @Override
  public User emulateCreate(UserRegistration dto) {
    String emailKey = dto.getEmail().toLowerCase();
    String userKey  = dto.getUsername().toLowerCase();

    // Block duplicates; controller later will show friendly messages
    if (existsByEmail(emailKey) || existsByUsername(userKey)) {
      throw new IllegalArgumentException("Duplicate");
    }

    Long id = seq.getAndIncrement();
    User u = new User();
    u.setId(id);
    u.setFirstName(dto.getFirstName().trim());
    u.setLastName(dto.getLastName().trim());
    u.setEmail(dto.getEmail().trim());
    u.setPhone(dto.getPhone().trim());
    u.setUsername(dto.getUsername().trim());
    // M2 only: keep "{noop}password" for simple compare
    u.setPasswordHash("{noop}" + dto.getPassword());

    byId.put(id, u);
    byEmail.put(emailKey, id);
    byUser.put(userKey, id);
    return u;
  }

  @Override public boolean existsByEmail(String email)   { return byEmail.containsKey(email.toLowerCase()); }
  @Override public boolean existsByUsername(String user) { return byUser.containsKey(user.toLowerCase()); }

  @Override
  public Optional<User> findByEmailOrUsername(String key) {
    String k = key.toLowerCase();
    Long id = byEmail.getOrDefault(k, byUser.get(k));
    return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
  }
}
