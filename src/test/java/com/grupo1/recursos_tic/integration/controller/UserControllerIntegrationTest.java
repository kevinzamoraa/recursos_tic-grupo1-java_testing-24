package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.UserService;
import com.grupo1.recursos_tic.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private UserService resourceService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll() throws Exception {
        /* TODO: Enable login config to allow access to user's list. It is redirecting to login view */
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void findById() {
    }

    @Test
    void getFormToCreateNewUser() {
    }

    @Test
    void getFormToEditUser() {
    }

    @Test
    void save() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void deleteAllUsers() {
    }
}