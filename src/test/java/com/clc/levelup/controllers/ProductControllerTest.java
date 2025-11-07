package com.clc.levelup.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    // This test verifies the /products route works when logged in
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturnProductsPage() throws Exception {
        mvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(view().name("products/list"));
    }

    // Optional: checks alternate mapping if you have /products/list
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void shouldReturnOkForProductsListAlias() throws Exception {
        mvc.perform(get("/products/list"))
            .andExpect(status().isOk());
    }
}

