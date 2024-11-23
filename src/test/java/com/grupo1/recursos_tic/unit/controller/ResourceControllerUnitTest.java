package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.controller.ResourceController;
import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerUnitTest {

    @InjectMocks
    private ResourceController resourceController;
    @Mock
    private ResourceService resourceService;
    @Mock
    private ResourceListsService resourceListsService;
    @Mock
    private RatingService ratingService;
    @Mock
    private Model model;

    @Test
    @DisplayName("findAll() utilizando mocks se prueba la interacción con servicio y model")
    void findAll() {

        Resource resource1 = Resource.builder().id(1L).build();
        Resource resource2 = Resource.builder().id(2L).build();
        List<Resource> resources = List.of(resource1, resource2);

        when(resourceService.findAll()).thenReturn(resources);

        String view = resourceController.findAll(model);

        assertEquals("resource/list", view);
        verify(resourceService).findAll();
        verify(this.model).addAttribute("resources", resources);
    }

    @Test
    @DisplayName("findById cuando el recurso SÍ existe")
    void findById_WhenManufacturerExists() {

        Long resourceId = 1L;
        Resource resource1 = Resource.builder()
                .id(resourceId)
                .title("Resource 1")
                .build();
        Optional<Resource> resourceOpt = Optional.of(resource1);

        when(resourceService.findById(resourceId)).thenReturn(resourceOpt);

        String view = resourceController.findById(model, resourceId);

        assertEquals("resource/detail", view);
        verify(resourceService).findById(resourceId);
        verify(model).addAttribute("resource", resource1);
    }

    @Test
    @DisplayName("findById cuando el recurso NO existe")
    void findById_WhenResourceDoesNotExist() {

        Long resourceId = 1L;
        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            resourceController.findById(model, resourceId);
        });

        verify(resourceService).findById(resourceId);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("findById con ID inválido")
    void findById_WithInvalidId() {
        Long invalidId = 0L;  // o -1L

        assertThrows(NoSuchElementException.class, () -> {
            resourceController.findById(model, invalidId);
        });

        verify(resourceService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

}

