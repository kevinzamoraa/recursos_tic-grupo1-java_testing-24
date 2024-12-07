package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.controller.ResourceController;
import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.service.*;
import com.grupo1.recursos_tic.util.ErrMsg;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Este test de integración se encarga de verificar que el controlador
 * ResourceController funciona correctamente.
 *
 * @author Javier Guerra
 * @version 1.1.0
 * @since 2024-12-08
 * Unit tests for {@link ResourceController} class.
 */
@SpringBootTest
@AutoConfigureMockMvc
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
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        resourceService.deleteAll();

        var admin = User.builder().name("Administrador").email("admin@admin.es").role(UserRole.ADMIN).username("admin")
                .imageUrl("/img/user/noUser.png").password(passwordEncoder.encode("Admin1234")).build();
        var user1 = User.builder().name("Javier").email("a@a.es").role(UserRole.AUTHOR).username("javier")
                .imageUrl("/img/user/javier.png").password(passwordEncoder.encode("User1234")).build();
        var user2 = User.builder().name("Kevin").email("b@b.es").role(UserRole.AUTHOR).username("kevin")
                .imageUrl("/img/user/kevin.jpeg").password(passwordEncoder.encode("User1234")).build();
        var user3 = User.builder().name("Marina").email("c@c.es").role(UserRole.AUTHOR).username("marina")
                .imageUrl("/img/user/marina.jpeg").password(passwordEncoder.encode("User1234")).build();

        userService.deleteAllUsers();
        userService.saveAll(List.of(admin, user1, user2, user3));
    }

    private void setUserAuth(String userName) {
        var user = userService.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario"));

        var auth = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }


    /*
     * Method: findAll(Model)
     */

    @Test
    @DisplayName("findAll() cuando SÍ existen recursos")
    void findAll_WhenResourceExists() throws Exception {
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
    @DisplayName("findAll() cuando NO existen recursos")
    void findAll_WhenResourcesDoesNotExist() throws Exception {
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

    @Test
    @DisplayName("findById cuando el recurso SÍ existe y el usuario SÍ está autenticado")
    void findById_WhenResourceExists_Authenticated() throws Exception {
        Resource resource = Resource.builder()
                .title("Recurso1")
                .tags(new HashSet<>())
                .build();
        resource = resourceService.save(resource);

        setUserAuth("javier");

        mockMvc.perform(get("/resources/" + resource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/detail"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("title", is("Recurso1"))
                ))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attributeExists("lists"));
    }

    @Test
    @DisplayName("findById cuando el recurso SÍ existe y el usuario NO está autenticado")
    void findById_WhenResourceExists_NotAuthenticated() throws Exception {
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
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attributeDoesNotExist("lists"));
    }

    @Test
    @DisplayName("findById cuando el recurso NO existe")
    void findById_WhenResourceDoesNotExist() throws Exception {
        mockMvc.perform(get("/resources/5"))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage(ErrMsg.NOT_FOUND);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("ratings"))
                .andExpect(model().attributeDoesNotExist("lists"));
    }

    @Test
    @DisplayName("findById cuando el ID del recurso no es válido por ser 0")
    void findById_WithInvalidId_zero() throws Exception {
        mockMvc.perform(get("/resources/0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("ratings"))
                .andExpect(model().attributeDoesNotExist("lists"));
    }

    @Test
    @DisplayName("findById cuando el ID del recurso no es válido por ser negativo")
    void findById_WithInvalidId_negative() throws Exception {
        mockMvc.perform(get("/resources/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("ratings"))
                .andExpect(model().attributeDoesNotExist("lists"));
    }

    /*
     * Method: getFormToCreate(Model)
     */

    @Test
    @DisplayName("getFormToCreate cuando SÍ se crea el recurso")
    void getFormToCreate_WhenResourceIsCreated() throws Exception {
        setUserAuth("javier");

        mockMvc.perform(get("/resources/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("id", is(nullValue()))
                ));
    }


    /*
     * Method: getFormToCreateNew(Model, Long)
     */

    @Test
    @DisplayName("getFormToCreateNew cuando se crea el recurso y se añade a la lista")
    void getFormToCreateNew_WhenListExist() throws Exception {
        setUserAuth("javier");

        ResourceList resourceList = ResourceList.builder()
                .owner(userService.findByUsername("javier").get())
                .name("Tecnología")
                .description("Mis favoritos tech")
                .resources(Set.of())
                .build();
        resourceListsService.save(resourceList);

        Long listId = resourceList.getId();

        mockMvc.perform(get("/resources/create/" + listId))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("id", is(nullValue()))
                ))
                .andExpect(model().attributeExists("listId"))
                .andExpect(model().attribute("listId", is(listId)));
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no existe")
    void getFormToCreateNew_WhenListDoesNotExist() throws Exception {
        setUserAuth("javier");

        long listId = 9999L;

        mockMvc.perform(get("/resources/create/" + listId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage(ErrMsg.NOT_FOUND);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no es válido por ser 0")
    void getFormToCreateNew_WithInvalidListId_zero() throws Exception {
        setUserAuth("javier");

        long invalidListId = 0L;

        mockMvc.perform(get("/resources/create/" + invalidListId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de la lista no es válido por ser negativo")
    void getFormToCreateNew_WithInvalidListId_negativo() throws Exception {
        setUserAuth("javier");

        long invalidListId = -1L;

        mockMvc.perform(get("/resources/create/" + invalidListId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando el recurso Sí existe")
    void getFormToUpdate_WhenResourceExist() throws Exception {
        setUserAuth("javier");

        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        resourceService.save(resource);

        Long resourceId = resource.getId();

        mockMvc.perform(get("/resources/update/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("id", is(resourceId))
                ));
    }

    @Test
    @DisplayName("getFormToUpdate cuando el recurso NO existe")
    void getFormToUpdate_WhenResourceDoesNotExist() throws Exception {
        setUserAuth("javier");

        long resourceId = 9999L;

        mockMvc.perform(get("/resources/update/" + resourceId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage(ErrMsg.NOT_FOUND);
                })
                .andExpect(model().attributeDoesNotExist("resource"));
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso no es válido por ser 0")
    void getFormToUpdate_WithInvalidResourceId_zero() throws Exception {
        setUserAuth("javier");

        long invalidId = 0L;

        mockMvc.perform(get("/resources/update/" + invalidId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"));
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del recurso no es válido por ser negativo")
    void getFormToUpdate_WithInvalidResourceId_negative() throws Exception {
        setUserAuth("javier");

        long invalidId = -1L;

        mockMvc.perform(get("/resources/update/" + invalidId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"));
    }

    /*
     * Method: getFormToUpdateAndList(Model, Long, Long)
     */

    @Test
    @DisplayName("getFormToUpdateAndList cuando el recurso y la lista Sí existen")
    void getFormToUpdateAndList_WhenResourceAndListExist() throws Exception {
        setUserAuth("javier");

        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        resourceService.save(resource);

        ResourceList resourceList = ResourceList.builder()
                .owner(userService.findByUsername("javier").get())
                .name("Tecnología")
                .description("Mis favoritos tech")
                .resources(Set.of(resource))
                .build();
        resourceListsService.save(resourceList);

        Long resourceId = resource.getId();
        Long listId = resourceList.getId();

        mockMvc.perform(get("/resources/update/" + resourceId + "/" + listId))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"))
                .andExpect(model().attribute("resource",
                        hasProperty("id", is(resourceId))
                ))
                .andExpect(model().attributeExists("listId"))
                .andExpect(model().attribute("listId", is(listId)));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el recurso NO existe")
    void getFormToUpdateAndList_WhenResourceDoesNotExist() throws Exception {
        setUserAuth("javier");

        ResourceList resourceList = ResourceList.builder()
                .owner(userService.findByUsername("javier").get())
                .name("Tecnología")
                .description("Mis favoritos tech")
                .resources(Set.of())
                .build();
        resourceListsService.save(resourceList);

        long resourceId = 9999L;
        Long listId = resourceList.getId();

        mockMvc.perform(get("/resources/update/" + resourceId + "/" + listId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage(ErrMsg.NOT_FOUND);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando la lista NO existe")
    void getFormToUpdateAndList_WhenResourceListDoesNotExist() throws Exception {
        setUserAuth("javier");

        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        resourceService.save(resource);

        Long resourceId = resource.getId();
        Long listId = 9999L;

        mockMvc.perform(get("/resources/update/" + resourceId + "/" + listId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(NoSuchElementException.class)
                            .hasMessage(ErrMsg.NOT_FOUND);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso no es válido por ser 0")
    void getFormToUpdateAndList_WithInvalidResourceId_zero() throws Exception {
        setUserAuth("javier");

        long invalidResourceId = 0L;
        long listId = 1L;

        mockMvc.perform(get("/resources/update/" + invalidResourceId + "/" + listId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido por ser 0")
    void getFormToUpdateAndList_WithInvalidListId_zero() throws Exception {
        setUserAuth("javier");

        long resourceId = 1L;
        long invalidListId = 0L;

        mockMvc.perform(get("/resources/update/" + resourceId + "/" + invalidListId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del recurso no es válido por ser negativo")
    void getFormToUpdateAndList_WithInvalidResourceId_negative() throws Exception {
        setUserAuth("javier");

        long invalidResourceId = -1L;
        long listId = 1L;

        mockMvc.perform(get("/resources/update/" + invalidResourceId + "/" + listId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido por ser negativo")
    void getFormToUpdateAndList_WithInvalidResourceORListId_negative() throws Exception {
        setUserAuth("javier");

        long resourceId = 1L;
        long invalidListId = -1L;

        mockMvc.perform(get("/resources/update/" + resourceId + "/" + invalidListId))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage(ErrMsg.INVALID_ID);
                })
                .andExpect(model().attributeDoesNotExist("resource"))
                .andExpect(model().attributeDoesNotExist("listId"));
    }

    /*
     * Method: save(Resource, Long)
     */

    @Test
    @DisplayName("save cuando el recurso y la lista SÍ existen")
    void save_WhenResourceExist() throws Exception {
        setUserAuth("javier");

        Resource resource = Resource.builder()
                .title("Recurso")
                .url("#")
                .type(ResourceType.DOCUMENT)
                .build();
        resourceService.save(resource);

        ResourceList resourceList = ResourceList.builder()
                .owner(userService.findByUsername("javier").get())
                .name("Tecnología")
                .description("Mis favoritos tech")
                .resources(Set.of(resource))
                .build();
        resourceListsService.save(resourceList);

        Long resourceId = resource.getId();
        Long listId = resourceList.getId();

        mockMvc.perform(post("/resources/update/" + resourceId + "/" + listId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Recurso Modificado")
                .param("url", "#")
                .param("type", ResourceType.DOCUMENT.name())
                .param("description", "Nueva descripción")
                );
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/resourcelists/" + listId)); // TODO
    }

    @Test
    @DisplayName("save cuando el recurso Sí existe y la lista NO existe")
    void save_WhenResourceListDoesNotExist() throws Exception {

//        long resourceId = 1L;
//        Long listId = null;
//        Resource resource = Resource.builder()
//                .id(resourceId)
//                .title("Recurso")
//                .url("#")
//                .type(ResourceType.DOCUMENT)
//                .build();
//
//        when(resourceService.findById(resourceId)).thenReturn(Optional.of(resource));
//
//        String view = resourceController.save(resource, null);
//
//        assertFalse(listId != null && listId <= 0);
//        assertNull(resourceController.formValidation(resource));
//        verify(resourceListsService, never()).findById(resourceId);
//        verify(resourceListsService, never()).save(any());
//        verify(resourceService).findById(resourceId);
//        verify(resourceService).save(resource);
//        assertEquals("redirect:/resources/1", view);
    }

    @Test
    @DisplayName("save cuando el recurso NO existe y la list SÍ existe")
    void save_WhenResourceDoesNotExist() throws Exception {

//        long resourceId = 1L;
//        Long listId = 1L;
//        Resource resource = Resource.builder()
//                .title("Recurso")
//                .url("#")
//                .type(ResourceType.DOCUMENT)
//                .build();
//        ResourceList resourceList = ResourceList.builder().id(listId).build();
//
//        when(resourceListsService.findById(listId)).thenReturn(Optional.of(resourceList));
//
//        String view = resourceController.save(resource, listId);
//
//        assertFalse(listId != null && listId <= 0);
//        assertNull(resourceController.formValidation(resource));
//        verify(resourceListsService).findById(resourceId);
//        verify(resourceListsService).save(resourceList);
//        verify(resourceService, never()).findById(resourceId);
//        verify(resourceService).save(resource);
//        assertEquals("redirect:/resourcelists/1", view);
    }

    @Test
    @DisplayName("save cuando el recurso NO existe y la lista NO existe")
    void save_WhenResourceAndListDoesNotExist() throws Exception {

//        long resourceId = 1L;
//        Long listId = null;
//        Resource resource = Resource.builder()
//                .title("Recurso")
//                .url("#")
//                .type(ResourceType.DOCUMENT)
//                .build();
//
//        // simular thenAnswer
//        doAnswer(invocation -> {
//            Resource resourceArg = invocation.getArgument(0);
//            resourceArg.setId(resourceId);
//            return resourceArg;
//        }).when(resourceService).save(resource);
//
//        String view = resourceController.save(resource, null);
//
//        assertFalse(listId != null && listId <= 0);
//        assertNull(resourceController.formValidation(resource));
//        verify(resourceService).save(resource);
//        verify(resourceListsService, never()).findById(resourceId);
//        verify(resourceListsService, never()).save(any());
//        verify(resourceService, never()).findById(resourceId);
//        assertEquals("redirect:/resources/1", view);
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido por ser 0")
    void save_WithInvalidListId_zero() throws Exception {

//        long resourceId = 1L;
//        Long invalidListId = 0L;
//        Resource resource = new Resource();
//        ResourceList invalidResourceList = new ResourceList();
//        ResourceController spyResourceController = Mockito.spy(resourceController);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> spyResourceController.save(resource, invalidListId));
//
//        assertTrue(invalidListId != null && invalidListId <= 0);
//        Mockito.verify(spyResourceController, Mockito.never()).formValidation(resource);
//        verify(resourceListsService, never()).findById(resourceId);
//        verify(resourceListsService, never()).save(invalidResourceList);
//        verify(resourceService, never()).findById(resourceId);
//        verify(resourceService, never()).save(resource);
//        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID de la lista no es válido por ser negativo")
    void save_WithInvalidListId_negative() throws Exception {

//        long resourceId = 1L;
//        Long invalidListId = -1L;
//        Resource resource = new Resource();
//        ResourceList invalidResourceList = new ResourceList();
//        ResourceController spyResourceController = Mockito.spy(resourceController);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> spyResourceController.save(resource, invalidListId));
//
//        assertTrue(invalidListId != null && invalidListId <= 0);
//        Mockito.verify(spyResourceController, Mockito.never()).formValidation(resource);
//        verify(resourceListsService, never()).findById(resourceId);
//        verify(resourceListsService, never()).save(invalidResourceList);
//        verify(resourceService, never()).findById(resourceId);
//        verify(resourceService, never()).save(resource);
//        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("save cuando la validación falla")
    void save_WhenValidationFail() throws Exception {

//        Resource resource = new Resource();
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
//                () -> resourceController.save(resource, null));
//
//        assertNotNull(resourceController.formValidation(resource));
//        verify(resourceListsService, never()).findById(anyLong());
//        verify(resourceListsService, never()).save(any(ResourceList.class));
//        verify(resourceService, never()).findById(anyLong());
//        verify(resourceService, never()).save(resource);
//        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    /*
     * Method: deleteById(Long)
     */

    @Test
    @DisplayName("deleteById cuando el ID del recurso Sí existe")
    void deleteById_WhenResourceExist() throws Exception {

//        Long resourceId = 1L;
//
//        when(resourceService.existsById(resourceId)).thenReturn(true);
//
//        String view = resourceController.deleteById(resourceId);
//
//        assertFalse(invalidIntPosNumber(resourceId) || resourceId == 0);
//        verify(resourceService).existsById(resourceId);
//        verify(resourceService).removeResourceWithDependencies(resourceId);
//        assertEquals("redirect:/resources", view);
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso NO existe")
    void deleteById_WhenResourceDoesNotExist() throws Exception {

//        Long resourceId = 1L;
//
//        when(resourceService.existsById(resourceId)).thenReturn(false);
//
//        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
//                () -> resourceController.deleteById(resourceId));
//
//        assertFalse(invalidIntPosNumber(resourceId) || resourceId == 0);
//        verify(resourceService).existsById(resourceId);
//        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
//        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso no es válido por ser 0")
    void deleteById_WithInvalidResourceId_zero() throws Exception {

//        Long invalidId = 0L;
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> resourceController.deleteById(invalidId));
//
//        assertTrue(invalidIntPosNumber(invalidId) || invalidId == 0);
//        verify(resourceService, never()).existsById(anyLong());
//        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
//        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando el ID del recurso no es válido por ser negativo")
    void deleteById_WithInvalidResourceId_negative() throws Exception {

//        Long invalidId = -1L;
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> resourceController.deleteById(invalidId));
//
//        assertTrue(invalidIntPosNumber(invalidId) || invalidId == 0);
//        verify(resourceService, never()).existsById(anyLong());
//        verify(resourceService, never()).removeResourceWithDependencies(anyLong());
//        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());
    }

    @Test
    @DisplayName("deleteById cuando se produce una excepción en el servicio")
    void deleteById_ServiceThrowsException() throws Exception {

//        long resourceId = 1L;
//
//        when(resourceService.existsById(resourceId)).thenReturn(true);
//
//        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(resourceService)
//                .removeResourceWithDependencies(resourceId);
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
//                () -> resourceController.deleteById(resourceId));
//
//
//        assertFalse(invalidIntPosNumber(resourceId) || resourceId == 0);
//        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    /*
     * Method: deleteAll(Model)
     */

    @Test
    @DisplayName("getFormToUpdate cuando SÍ se han borrado los recursos o si hay 0 (cero) recursos")
    void deleteAll_WhenResourcesExists() throws Exception {

//        when(resourceService.count()).thenReturn(0L);
//
//        String view = resourceController.deleteAll();
//
//        verify(resourceService).deleteAll();
//        assertEquals(0L, resourceService.count());
//        assertEquals("redirect:/resources", view);
    }

    @Test
    @DisplayName("getFormToUpdate cuando se produce una excepción al borrar")
    void deleteAll_WhenDeleteException() throws Exception {

//        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(resourceService).deleteAll();
//
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
//                () -> resourceController.deleteAll());
//
//        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("getFormToUpdate cuando quedan recursos por borrar")
    void deleteAll_WhenDoesNotDelete() throws Exception {

//        when(resourceService.count()).thenReturn(5L);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> resourceController.deleteAll());
//
//        assertEquals(ErrMsg.NOT_DELETED, exception.getMessage());
    }

}