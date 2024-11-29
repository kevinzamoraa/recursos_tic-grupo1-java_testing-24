package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;
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
public class ResourceControllerIntegrationTest {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceListsService resourceListsService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/list"));
    }

}

