package com.clc.levelup.service;

import com.clc.levelup.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

// Starts the app context, runs DataSeed, and checks the demo login.
@SpringBootTest
class InMemoryAuthServiceTest {

  @Autowired AuthService auth;

  @Test
  void demoUserAuthenticates() {
    LoginRequest req = new LoginRequest();
    req.setEmailOrUsername("demo@clc.com");
    req.setPassword("pass1234");
    assertTrue(auth.authenticate(req));
  }
}

