package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;
import com.grupo1.recursos_tic.service.UserDetailsServiceImpl;
import com.grupo1.recursos_tic.util.ErrMsg;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

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
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    /*
     * Method: findAll(Model)
     */

    @Test
    @DisplayName("Buscar todos los recursos")
    void findAll() throws Exception {
        resourceService.saveAll(List.of(
                Resource.builder().title("Recurso1").build(),
                Resource.builder().title("Recurso2").build()
        ));

        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/list"))
                .andExpect(model().attributeExists("resources"))
                .andExpect(model().attribute("resources", hasSize(2)))
                .andExpect(model().attribute("resources",
                        allOf(
                                hasItem(hasProperty("title", is("Recurso1"))),
                                hasItem(hasProperty("title", is("Recurso2")))
                        )
                ));
    }

    @Test
    @DisplayName("Buscar todos los recursos y no obtener recursos")
    void findAll_NoResources() throws Exception {
        resourceService.saveAll(List.of());

        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/list"))
                .andExpect(model().attributeExists("resources"))
                .andExpect(model().attribute("resources", hasSize(0)));
    }

    /*
     * Method: findById(Model, Long)
     */

    // TODO autenticación
    @Test
    @DisplayName("Buscar recurso con ID válido y usuario autenticado")
    //@WithMockUser()
    void findById_WithAuthenticated() throws Exception {

        Resource resource = Resource.builder()
                .title("Recurso1")
                .tags(new HashSet<>())
                .build();
        resource = resourceService.save(resource);

        mockMvc.perform(get("/resources/" + resource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/detail"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("title", is("Recurso1"))
                ))
                .andExpect(model().attributeExists("ratings"));
                //.andExpect(model().attributeExists("lists")); // Sólo con autenticación

        // TODO revisar .andExpect que faltan
    }

    @Test
    @DisplayName("Buscar un recurso con ID válido y usuario no autenticado")
    void findById_WithoutAuthenticated() throws Exception {
        Resource resource = Resource.builder()
                .title("Recurso1")
                .tags(new HashSet<>())
                .build();
        resourceService.save(resource);

        mockMvc.perform(get("/resources/" + resource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/detail"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("title", is("Recurso1"))
                ))
                .andExpect(model().attributeExists("ratings"));

        // TODO revisar .andExpect que faltan
    }

    @Test
    @DisplayName("Buscar recurso con ID no válido por ser 0")
    void findById_InvalidId_zero() throws Exception {
        mockMvc.perform(get("/resources/0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                });
    }

    @Test
    @DisplayName("Buscar recurso con ID no válido por ser negativo")
    void findById_InvalidId_negative() throws Exception {
        mockMvc.perform(get("/resources/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                });
    }

}