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
import org.springframework.web.bind.annotation.PathVariable;

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


    /*
     * Method: findAll(Model)
     */

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

    /*
     * Method: findById(Model, Long)
     */

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
        verify(model, never()).addAttribute(anyString(), any()); // No se añade nada al modelo
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

    /*
     * Method: getFormToCreate(Model)
     */

    @Test
    @DisplayName("getFormToCreate cuando se crea el recurso")
    void getFormToCreate() {

        String view = resourceController.getFormToCreate(model);

        assertEquals("resource/form", view);
        verify(model).addAttribute(eq("resource"), any(Resource.class)); // ? eq()
    }

    /*
     * Method: getFormToCreateNew(Model, Long)
     */

    @Test
    @DisplayName("getFormToCreateNew cuando se crea el recurso y se añade a la lista")
    void getFormToCreateNew_WhenListExist() {
        Long ListId = 1L;
        when(resourceListsService.existsById(ListId)).thenReturn(true);

        String view = resourceController.getFormToCreateNew(model, ListId);

        assertEquals("resource/form", view);
        verify(resourceListsService).existsById(ListId);
        verify(model).addAttribute(eq("resource"), any(Resource.class));
        verify(model).addAttribute(eq("listId"), eq(ListId));
    }

    @Test
    @DisplayName("getFormToCreateNew cuando la lista no existe")
    void getFormToCreateNew_WhenListDoesNotExist() {
        Long listId = 1L;

        when(resourceListsService.existsById(listId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> {
            resourceController.getFormToCreateNew(model, listId);
        });

        verify(resourceListsService).existsById(listId);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew con ID de lista inválido")
    void getFormToCreateNew_WithInvalidListId() {
        Long invalidListId = 0L;

        assertThrows(NoSuchElementException.class, () -> {
            resourceController.getFormToCreateNew(model, invalidListId);
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew con ID no numérico")
    void getFormToCreateNew_WithNonNumericId() {
        assertThrows(NumberFormatException.class, () -> {
            // Intentamos convertir un texto a Long para simular lo que ocurriría en el controlador
            resourceController.getFormToCreateNew(model, Long.parseLong("abc"));
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew con ID de lista null")
    void getFormToCreateNew_WithNullListId() {
        Long nullListId = null;

        assertThrows(NoSuchElementException.class, () -> {
            resourceController.getFormToCreateNew(model, nullListId);
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando se actualiza el recurso")
    void getFormToUpdate() {
        //
    }

}

