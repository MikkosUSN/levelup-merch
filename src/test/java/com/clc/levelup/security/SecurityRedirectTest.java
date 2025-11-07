package com.clc.levelup.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security integration tests.
 * Updated for final config:
 * - /products is public (no redirect).
 * - Authenticated users can access it as well.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRedirectTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousUser_canAccessProductsWithoutRedirect() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void authenticatedUser_canAlsoAccessProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }
}
