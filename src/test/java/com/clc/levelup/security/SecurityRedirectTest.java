package com.clc.levelup.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser; // <-- required import

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRedirectTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void unauthenticated_isRedirectedToLogin() throws Exception {
    mockMvc.perform(get("/products"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser(username = "user", roles = { "USER" })
  void authenticatedUser_canAccessProducts() throws Exception {
    mockMvc.perform(get("/products"))
      .andExpect(status().isOk());
  }
}
