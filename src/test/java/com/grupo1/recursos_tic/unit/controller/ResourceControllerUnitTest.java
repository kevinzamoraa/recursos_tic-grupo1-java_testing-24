package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.controller.ResourceController;
import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.Resource;
import com.grupo1.recursos_tic.model.ResourceList;
import com.grupo1.recursos_tic.model.ResourceType;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.ResourceListsService;
import com.grupo1.recursos_tic.service.ResourceService;
import com.grupo1.recursos_tic.util.ErrMsg;
import com.grupo1.recursos_tic.util.Utility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Este test se encarga de verificar que el controlador ResourceController
 * funciona correctamente.
 * @author Javier Guerra
 * @version 1.1.0
 * @since 2024-11-28
 * Unit tests for {@link ResourceController} class.
 */
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
    private Utility utility; // TODO revisar si es necesario
    @Mock
    private Model model;

    /*
     * Method: formValidation(Resource)
     */

    @Test
    @DisplayName("formValidation() cuando el título es válido")
    void formValidation_WhenTitleIsValid() {
        Resource resource = Resource.builder().title("Titulo").url("#").type(ResourceType.DOCUMENT).build();

        String result = resourceController.formValidation(resource);

        assertEquals(null, result);
    }

    @Test
    @DisplayName("formValidation() cuando el título no es válido")
    void formValidation_WhenTitleIsInvalid() {
        Resource resource = Resource.builder().url("#").type(ResourceType.DOCUMENT).build();

        String result = resourceController.formValidation(resource);

        assertEquals("Falta el título", result);
    }

    @Test
    @DisplayName("formValidation() cuando la URL no es válida")
    void formValidation_WhenUrlIsInvalid() {
        Resource resource = Resource.builder().title("Titulo").type(ResourceType.DOCUMENT).build();

        String result = resourceController.formValidation(resource);

        assertEquals("Faltan la URL", result);
    }

    @Test
    @DisplayName("formValidation() cuando el tipo de recurso no es válido")
    void formValidation_WhenInvalidType() {

        Resource resource = Resource.builder().title("Titulo").url("#").type(ResourceType.INVALID).build();

        String result = resourceController.formValidation(resource);

        assertEquals("Falta el tipo de recurso", result);
    }

    @Test
    @DisplayName("formValidation() cuando el tipo de recurso es nulo")
    void formValidation_WhenNullType() {

        Resource resource = Resource.builder().title("Titulo").url("#").type(null).build();

        String result = resourceController.formValidation(resource);

        assertEquals("Falta el tipo de recurso", result);
    }

    /*
     * Method: findAll(Model)
     */

    @Test
    @DisplayName("findAll() cuando SÍ existen recursos")
    void findAll_WhenResourceExists() {
        Resource resource1 = Resource.builder().id(1L).build();
        Resource resource2 = Resource.builder().id(2L).build();
        List<Resource> resources = List.of(resource1, resource2);

        when(resourceService.findAll()).thenReturn(resources);

        String view = resourceController.findAll(model);

        verify(resourceService).findAll();
        verify(this.model).addAttribute("resources", resources);
        assertEquals("resource/list", view);
    }

    @Test
    @DisplayName("findAll() cuando NO existen recursos")
    void findAll_WhenResourcesDoesNotExist() {
        List<Resource> resources = new ArrayList<>();

        when(resourceService.findAll()).thenReturn(resources);

        String view = resourceController.findAll(model);

        verify(resourceService).findAll();
        verify(this.model).addAttribute("resources", resources);
        assertEquals("resource/list", view);
    }

    /*
     * Method: findById(Model, Long)
     */

    // TODO Probar Model Ratings y ResourceLists, y cuando isAuth()
    @Test
    @DisplayName("findById cuando el recurso SÍ existe")
    void findById_WhenResourceExists() {
        Long resourceId = 1L;
        Resource resource = Resource.builder()
                .id(resourceId)
                .title("Resource 1")
                .build();
        Optional<Resource> resourceOpt = Optional.of(resource);
        List<Rating> ratings = List.of();
        List<ResourceList> resourceLists = List.of();

        when(resourceService.findById(resourceId)).thenReturn(resourceOpt);
        when(ratingService.findAllByResource_Id(resourceId)).thenReturn(ratings);
        //when(utility.isAuth()).thenReturn(true); // TODO
        //when(resourceListsService.findByOwnerIdAndResourcesId(1L, resourceId)).thenReturn(resourceLists); // TODO

        String view = resourceController.findById(model, resourceId);

        verify(resourceService).findById(resourceId);
        verify(ratingService).findAllByResource_Id(resourceId);
        //verify(resourceListsService).findByOwnerIdAndResourcesId(1L, resourceId); // TODO
        verify(model).addAttribute("resource", resource);
        verify(model).addAttribute("ratings", ratings);
        //verify(model).addAttribute("lists", resourceLists); // TODO
        assertEquals("resource/detail", view);
    }

    @Test
    @DisplayName("findById cuando el recurso NO existe")
    void findById_WhenResourceDoesNotExist() {
        Long resourceId = 1L;
        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            resourceController.findById(model, resourceId);
        });

        verify(resourceService).findById(resourceId);
        verify(ratingService, never()).findAllByResource_Id(anyLong());
        verify(resourceListsService, never()).findByOwnerIdAndResourcesId(anyLong(), anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("findById cuando el ID del recurso no es válido")
    void findById_WithInvalidId() {
        Long invalidId = 0L;  // o -1L

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, invalidId);
        });

        verify(resourceService, never()).findById(anyLong());
        verify(ratingService, never()).findAllByResource_Id(anyLong());
        verify(resourceListsService, never()).findByOwnerIdAndResourcesId(anyLong(), anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("findById cuando el ID del recurso no es numérico")
    void findById_WithNonNumericId() {

        // TODO Tal vez IllegalArgumentException
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, Long.parseLong("abc"));
        });

        verify(resourceService, never()).findById(anyLong());
        verify(ratingService, never()).findAllByResource_Id(anyLong());
        verify(resourceListsService, never()).findByOwnerIdAndResourcesId(anyLong(), anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals("For input string: \"abc\"", exception.getMessage());
        // assertEquals(ErrMsg.INVALID_ID, exception.getMessage()); // TODO ?? cadena devuelta
    }

    @Test
    @DisplayName("findById cuando el ID del recurso es null")
    void findById_WithNullId() {

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, null);
        });

        verify(resourceService, never()).findById(anyLong());
        verify(ratingService, never()).findAllByResource_Id(anyLong());
        verify(resourceListsService, never()).findByOwnerIdAndResourcesId(anyLong(), anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: getFormToCreate(Model)
     */

    @Test
    @DisplayName("getFormToCreate cuando se crea el recurso")
    void getFormToCreate_WhenResourceIsCreated() {

        String view = resourceController.getFormToCreate(model);

        verify(model).addAttribute(eq("resource"), any(Resource.class));
        assertEquals("resource/form", view);
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

        verify(resourceListsService).existsById(ListId);
        verify(model).addAttribute(eq("resource"), any(Resource.class));
        verify(model).addAttribute(eq("listId"), eq(ListId));
        assertEquals("resource/form", view);
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no existe")
    void getFormToCreateNew_WhenListDoesNotExist() {
        Long listId = 1L;

        when(resourceListsService.existsById(listId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            resourceController.getFormToCreateNew(model, listId);
        });

        verify(resourceListsService).existsById(listId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no es válido")
    void getFormToCreateNew_WithInvalidListId() {
        Long invalidListId = 0L;

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.getFormToCreateNew(model, invalidListId);
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no es numérico")
    void getFormToCreateNew_WithNonNumericId() {

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.getFormToCreateNew(model, Long.parseLong("abc"));
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals("For input string: \"abc\"", exception.getMessage());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de lista es null")
    void getFormToCreateNew_WithNullListId() {

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.getFormToCreateNew(model, null);
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso Sí existe")
    void getFormToUpdate_WhenResourceExist() {
        Long resourceId = 1L;
        Resource resource = Resource.builder().id(resourceId).build();
        when(resourceService.findById(1L)).thenReturn(Optional.of(resource));

        String view = resourceController.getFormToUpdate(model, 1L);

        verify(resourceService).findById(1L);
        verify(model).addAttribute("resource", resource);
        assertEquals("resource/form", view);
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso NO existe")
    void getFormToUpdate_WhenResourceDoesNotExist() {
        Long resourceId = 1L;

        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            resourceController.findById(model, resourceId);
        });

        verify(resourceService).findById(resourceId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso no es válido")
    void getFormToUpdate_WithInvalidResourceId() {
        Long invalidId = 0L;

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, invalidId);
        });

        verify(resourceService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());//
    }

    @Test
    @DisplayName("getFormToUpdate cunado el ID del recurso no es numérico")
    void getFormToUpdate_WithNonNumericId() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, Long.parseLong("abc"));
        });

        verify(resourceService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals("For input string: \"abc\"", exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso es nulo")
    void getFormToUpdate_WithNullResourceId() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.findById(model, null);
        });

        verify(resourceService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: getFormToUpdateAndList(Model, Long, Long)
     */

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso Sí existe")
    void getFormToUpdateAndList_WhenResourceExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso NO existe")
    void getFormToUpdateAndList_WhenResourceDoesNotExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso no es válido")
    void getFormToUpdateAndList_WithInvalidResourceId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cunado el ID del recurso no es numérico")
    void getFormToUpdateAndList_WithNonNumericId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso es nulo")
    void getFormToUpdateAndList_WithNullResourceId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista Sí existe")
    void getFormToUpdateAndList_WhenResourceListExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista NO existe")
    void getFormToUpdateAndList_WhenResourceListDoesNotExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido")
    void getFormToUpdateAndList_WithInvalidResourceListId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cunado el ID de la lista no es numérico")
    void getFormToUpdateAndList_WithNonNumericListId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista es nulo")
    void getFormToUpdateAndList_WithNullResourceListId() {
        //
    }

    /*
     * Method: save(Resource, Long)
     */

    @Test
    @DisplayName("save cuando el recurso Sí existe")
    void save_WhenResourceExist() {
        //
    }

    @Test
    @DisplayName("save cuando el recurso NO existe")
    void save_WhenResourceDoesNotExist() {
        //
    }

    @Test
    @DisplayName("save cuando el recurso no es válido")
    void save_WithInvalidResourceId() {
        //
    }

    @Test
    @DisplayName("save cuando el recurso es nulo")
    void save_WithNullResourceId() {
        //
    }

    @Test
    @DisplayName("save cuando el ID de la lista Sí existe")
    void save_WhenResourceListExist() {
        //
    }

    @Test
    @DisplayName("save cuando el ID de la lista NO existe")
    void save_WhenResourceListDoesNotExist() {
        //
    }

    @Test
    @DisplayName("save cuando el ID de la lista no es válido")
    void save_WithInvalidResourceListId() {
        //
    }

    @Test
    @DisplayName("save cunado el ID de la lista no es numérico")
    void save_WithNonNumericListId() {
        //
    }

    @Test
    @DisplayName("save cuando el ID de la lista es nulo")
    void save_WithNullResourceListId() {
        //
    }

    /*
     * Method: deleteById(Long)
     */

    @Test
    @DisplayName("deleteById cuando el ID del recurso Sí existe")
    void deleteById_WhenResourceExist() {
        Long resourceId = 1L;

        when(resourceService.existsById(resourceId)).thenReturn(true);

        String view = resourceController.deleteById(resourceId);

        verify(resourceService).existsById(resourceId);
        verify(resourceService).removeResourceWithDependencies(resourceId);
        assertEquals("redirect:/resources", view);
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso NO existe")
    void deleteById_WhenResourceDoesNotExist() {
        Long resourceId = 1L;

        when(resourceService.existsById(resourceId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            resourceController.deleteById(resourceId);
        });

        verify(resourceService).existsById(resourceId);
        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso no es válido")
    void deleteById_WithInvalidResourceId() {
        Long invalidId = 0L;

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.deleteById(invalidId);
        });

        verify(resourceService, never()).existsById(anyLong());
        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso no es numérico")
    void deleteById_WithNonNumericId() {
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.deleteById(Long.valueOf("abc"));
        });

        verify(resourceService, never()).existsById(anyLong());
        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
        assertEquals("For input string: \"abc\"", exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso es nulo")
    void deleteById_WithNullResourceId() {

        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> {
            resourceController.deleteById(null);
        });

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: deleteAll(Model)
     */

    @Test
    @DisplayName("getFormToUpdate cuando SÍ se han borrado los recursos o si hay 0 (cero) recursos")
    void deleteAll_WhenResourcesExists() {

        when(resourceService.count()).thenReturn(0L);

        String view = resourceController.deleteAll();

        verify(resourceService).deleteAll();
        assertEquals(0L, resourceService.count());
        assertEquals("redirect:/resources", view);
    }

    @Test
    @DisplayName("getFormToUpdate cuando NO se han borrado los recursos")
    void deleteAll_WhenResourcesDoesNotExist() {

        when(resourceService.count()).thenReturn(5L); // TODO ¿Cómo probar que realmente se borra?

        // TODO Qué excepción se debe usar? SQLException?
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            resourceController.deleteAll();
        });

        verify(resourceService).deleteAll();
        assertNotEquals(0L, resourceService.count());
        assertEquals(ErrMsg.NOT_DELETED, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando se produce una excepción en el servicio")
    void deleteAll_ServiceThrowsException() {

        doThrow(new RuntimeException("Service error")).when(resourceService).deleteAll(); // TODO Service error

        assertThrows(RuntimeException.class, () -> {
            resourceController.deleteAll();
        });
    }

}

