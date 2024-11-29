package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.controller.ResourceController;
import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.service.*;
import com.grupo1.recursos_tic.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Este test se encarga de verificar que el controlador ResourceController
 * funciona correctamente.
 *
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
    private Model model;

    /*
     * Method: formValidation(Resource)
     */

    @Test
    @DisplayName("formValidation() cuando el título es válido")
    void formValidation_WhenTitleIsValid() {
        Resource resource = Resource.builder().title("Titulo").url("#").type(ResourceType.DOCUMENT).build();

        String result = resourceController.formValidation(resource);

        assertNull(result);
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

    @Test
    @DisplayName("findById cuando el recurso SÍ existe")
    void findById_WhenResourceExists() {
        Long resourceId = 1L;
        Resource resource = Resource.builder().id(resourceId).build();
        Optional<Resource> resourceOpt = Optional.of(resource);
        List<Rating> ratings = List.of();
        List<ResourceList> resourceLists = List.of();

        when(resourceService.findById(resourceId)).thenReturn(resourceOpt);
        when(ratingService.findAllByResource_Id(resourceId)).thenReturn(ratings);
        when(resourceListsService.findByOwnerIdAndResourcesId(
                1L, resourceId)).thenReturn(resourceLists);

        try (MockedStatic<Utility> mockedStatic = mockStatic(Utility.class)) {
            mockedStatic.when(Utility::isAuth).thenReturn(true);
            User user = User.builder().id(1L).build();
            mockedStatic.when(Utility::userAuth).thenReturn(Optional.of(user));

            String view = resourceController.findById(model, resourceId);

            when(Utility.isAuth()).thenReturn(true);
            verify(resourceService).findById(resourceId);
            verify(ratingService).findAllByResource_Id(resourceId);
            verify(resourceListsService).findByOwnerIdAndResourcesId(1L, resourceId);
            verify(model).addAttribute("resource", resource);
            verify(model).addAttribute("ratings", ratings);
            assertEquals("resource/detail", view);
        }
    }

    @Test
    @DisplayName("findById cuando el recurso NO existe")
    void findById_WhenResourceDoesNotExist() {
        long resourceId = 1L;
        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.findById(model, resourceId));

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

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.findById(model, invalidId));

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
    @DisplayName("getFormToCreate cuando SÍ se crea el recurso")
    void getFormToCreate_WhenResourceIsCreated() {

        String view = resourceController.getFormToCreate(model);

        verify(model).addAttribute(eq("resource"), any(Resource.class));
        assertEquals("resource/form", view);
    }

    @Test
    @DisplayName("getFormToCreate cuando NO se crea el recurso")
    void getFormToCreate_WhenResourceIsNotCreated() {

        ResourceController spyController = Mockito.spy(resourceController);

        Mockito.doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
                .when(spyController)
                .getFormToCreate(model);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> spyController.getFormToCreate(model));

        verify(model, never()).addAttribute(eq("resource"), any(Resource.class));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
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

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.getFormToCreateNew(model, listId));

        verify(resourceListsService).existsById(listId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no es válido")
    void getFormToCreateNew_WithInvalidListId() {
        Long invalidListId = 0L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.getFormToCreateNew(model, invalidListId));

        verify(resourceListsService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando el recurso Sí existe")
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
    @DisplayName("getFormToUpdate cuando el recurso NO existe")
    void getFormToUpdate_WhenResourceDoesNotExist() {
        long resourceId = 1L;

        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.findById(model, resourceId));

        verify(resourceService).findById(resourceId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso no es válido")
    void getFormToUpdate_WithInvalidResourceId() {
        Long invalidId = 0L;

        // TODO Se comprueba Id ilegal, pero en el método sale como no testeado
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.getFormToUpdate(model, invalidId));

        verify(resourceService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());//
    }

    /*
     * Method: getFormToUpdateAndList(Model, Long, Long)
     */

    @Test
    @DisplayName("getFormToUpdateAndList cuando el recurso y la lista Sí existen")
    void getFormToUpdateAndList_WhenResourceAndListExist() {
        long resourceId = 1L;
        Long ListId = 1L;
        Resource resource = Resource.builder().id(resourceId).build();
        when(resourceListsService.existsById(ListId)).thenReturn(true);
        when(resourceService.findById(resourceId)).thenReturn(Optional.of(resource));

        String view = resourceController.getFormToUpdateAndList(model, resourceId, ListId);

        verify(resourceListsService).existsById(ListId);
        verify(resourceService).findById(resourceId);
        verify(model).addAttribute(eq("resource"), any(Resource.class));
        verify(model).addAttribute(eq("listId"), eq(ListId));
        assertEquals("resource/form", view);
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el recurso NO existe")
    void getFormToUpdateAndList_WhenResourceDoesNotExist() {
        long resourceId = 1L;
        Long ListId = 1L;

        when(resourceListsService.existsById(ListId)).thenReturn(true);
        when(resourceService.findById(resourceId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.getFormToUpdateAndList(model, resourceId, ListId));

        verify(resourceListsService).existsById(ListId);
        verify(resourceService).findById(resourceId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando la lista NO existe")
    void getFormToUpdateAndList_WhenResourceListDoesNotExist() {
        long resourceId = 1L;
        Long ListId = 1L;

        when(resourceListsService.existsById(ListId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.getFormToUpdateAndList(model, resourceId, ListId));

        verify(resourceListsService).existsById(ListId);
        verify(resourceService, never()).findById(resourceId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso o" +
            "el ID de la lista no son válidos")
    void getFormToUpdateAndList_WithInvalidResourceORListId() {
        long invalidResourceId = 0L;
        Long invalidListId = 0L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.getFormToUpdateAndList(
                        model, invalidResourceId, invalidListId));

        verify(resourceListsService, never()).existsById(invalidListId);
        verify(resourceService, never()).findById(invalidResourceId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    /*
     * Method: save(Resource, Long)
     */

    @Test
    @DisplayName("save cuando el recurso y la lista SÍ existen")
    void save_WhenResourceExist() {
        long resourceId = 1L;
        Long ListId = 1L;
        Resource resource = Resource.builder()
                .id(resourceId)
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        ResourceList resourceList = ResourceList.builder().id(ListId).build();

        when(resourceService.findById(resourceId)).thenReturn(Optional.of(resource));

        String view = resourceController.save(resource, ListId);

        verify(resourceListsService, never()).findById(resourceId);
        verify(resourceListsService, never()).save(resourceList);
        verify(resourceService).findById(resourceId);
        verify(resourceService).save(resource);
        assertEquals("redirect:/resourcelists/1", view);
    }

    @Test
    @DisplayName("save cuando el recurso Sí existe y la lista NO existe")
    void save_WhenResourceListDoesNotExist() {

        // TODO
        //assertEquals("redirect:/resources/1", view);
    }

    @Test
    @DisplayName("save cuando el recurso NO existe")
    void save_WhenResourceDoesNotExist() {
        long resourceId = 1L;
        Long ListId = 1L;
        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        ResourceList resourceList = ResourceList.builder().id(ListId).build();

        when(resourceListsService.findById(ListId)).thenReturn(Optional.of(resourceList));

        String view = resourceController.save(resource, ListId);

        verify(resourceListsService).findById(resourceId);
        verify(resourceListsService).save(resourceList);
        verify(resourceService, never()).findById(resourceId);
        verify(resourceService).save(resource);
        assertEquals("redirect:/resourcelists/1", view);
    }

    @Test
    @DisplayName("save cuando el recurso NO existe y la lista NO existe")
    void save_WhenResourceAndListDoesNotExist() {
        long resourceId = 1L;
        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();

        // simular thenAnswer
        doAnswer(invocation -> {
            Resource resourceArg = invocation.getArgument(0);
            resourceArg.setId(resourceId);
            return resourceArg;
        }).when(resourceService).save(resource);

        String view = resourceController.save(resource, null);

        verify(resourceService).save(resource);
        verify(resourceListsService, never()).findById(resourceId);
        verify(resourceListsService, never()).save(any());
        verify(resourceService, never()).findById(resourceId);
        assertEquals("redirect:/resources/1", view);
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido")
    void save_WithInvalidListId() {
        long resourceId = 1L;
        Long invalidListId = 0L;
        Resource resource = new Resource();
        ResourceList invalidResourceList = new ResourceList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.save(resource, invalidListId));

        verify(resourceListsService, never()).findById(resourceId);
        verify(resourceListsService, never()).save(invalidResourceList);
        verify(resourceService, never()).findById(resourceId);
        verify(resourceService, never()).save(resource);
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
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

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> resourceController.deleteById(resourceId));

        verify(resourceService).existsById(resourceId);
        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso no es válido")
    void deleteById_WithInvalidResourceId() {
        Long invalidId = 0L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resourceController.deleteById(invalidId));

        verify(resourceService, never()).existsById(anyLong());
        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando se produce una excepción en el servicio")
    void deleteById_ServiceThrowsException() {
        long ResourceId = 1L;

        when(resourceService.existsById(ResourceId)).thenReturn(true);

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(resourceService)
                .removeResourceWithDependencies(ResourceId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> resourceController.deleteById(ResourceId));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
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
    @DisplayName("getFormToUpdate cuando se produce una excepción en el servicio")
    void deleteAll_ServiceThrowsException() {

        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(resourceService).deleteAll();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> resourceController.deleteAll());

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    // TODO NoSuchElementException en deleteAll deleteAll(Model)

}