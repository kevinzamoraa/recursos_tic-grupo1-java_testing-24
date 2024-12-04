package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.model.ResourceType;
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

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
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
    @DisplayName("Buscar un recurso por su ID")
    void findById() throws Exception {
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
    }

//    @Test
//    @DisplayName("Buscar recurso con ID inválido")
//    void findById_InvalidId() throws Exception {
//        mockMvc.perform(get("/resources/0"))
//                .andExpect(status().isBadRequest()) // Suponiendo que manejas esto con un 400 Bad Request
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
//    }

//    @Test
//    @DisplayName("Buscar recurso no existente")
//    void findById_NotFound() throws Exception {
//        mockMvc.perform(get("/resources/99999"))
//                .andExpect(status().isNotFound());
//    }

//    @Test
//    @DisplayName("Verificar atributos del modelo cuando hay listas del usuario")
//    void findById_WithUserLists() throws Exception {
//        // Configurar un escenario con usuario autenticado y listas
//        // Esto puede requerir configurar el contexto de seguridad
//        Authentication auth = setupMockAuthentication();
//        User user = (User) auth.getPrincipal();
//
//        var resource = Resource.builder()
//                .title("Recurso Con Listas")
//                .url("http://ejemplo.com")
//                .type(ResourceType.DOCUMENT)
//                .build();
//        resource = resourceService.save(resource);
//
//        mockMvc.perform(get("/resources/" + resource.getId()))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("lists"));
//    }

//    @Test
//    @DisplayName("Verificar manejo de ratings para un recurso")
//    void findById_CheckRatings() throws Exception {
//        var resource = Resource.builder()
//                .title("Recurso Para Ratings")
//                .url("http://ejemplo.com")
//                .type(ResourceType.DOCUMENT)
//                .build();
//        resource = resourceService.save(resource);
//
//        // Agregar algunos ratings al recurso
//        Rating rating1 = new Rating();
//        rating1.setResource(resource);
//        ratingService.save(rating1);
//
//        mockMvc.perform(get("/resources/" + resource.getId()))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("ratings", hasSize(1)));
//    }

}

