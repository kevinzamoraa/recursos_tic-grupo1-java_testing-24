package com.grupo1.recursos_tic.unit.controller;

import com.grupo1.recursos_tic.controller.UserController;
import com.grupo1.recursos_tic.integration.controller.UserControllerIntegrationTest;
import com.grupo1.recursos_tic.model.*;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.UserService;
import com.grupo1.recursos_tic.util.ErrMsg;
import com.grupo1.recursos_tic.util.Utility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private UserControllerIntegrationTest controller;
    @Mock
    private Model model;

    private Utility utility;

    /*
     * Method: findAll(Model)
     */

    @Test
    @DisplayName("findAll() cuando SÍ existen usuarios")
    void findAll_WhenUserExists() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        List<User> users = List.of(user1, user2);

        when(userService.findAll()).thenReturn(users);

        String view = userController.findAll(model);

        verify(userService).findAll();
        verify(this.model).addAttribute("users", users);
        assertEquals("user/list", view);
    }

    @Test
    @DisplayName("findAll() cuando NO existen usuarios")
    void findAll_WhenUsersDoesNotExist() {
        List<User> users = new ArrayList<>();

        when(userService.findAll()).thenReturn(users);

        String view = userController.findAll(model);

        verify(userService).findAll();
        verify(this.model).addAttribute("users", users);
        assertEquals("user/list", view);
    }

    /*
     * Method: findById(Model, Long)
     */

    @Test
    @DisplayName("findById cuando el usuario SÍ existe")
    void findById_WhenUserExists() {
        Long userId = 1L;
        User user1 = User.builder()
                .id(userId)
                .name("User 1")
                .build();
        Optional<User> userOpt = Optional.of(user1);

        when(UserService.findById(userId)).thenReturn(userOpt);

        String view = userController.findById(userId, model);

        verify(userService).findById(userId);
        verify(model).addAttribute("user", user1);
        assertEquals("user/detail", view);
    }

    @Test
    @DisplayName("findById cuando el usuario NO existe")
    void findById_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userService.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            userController.findById(userId, model);
        });

        verify(userService).findById(userId);
        verify(model, never()).addAttribute(anyString(), any()); // No se añade nada al modelo
    }

    @Test
    @DisplayName("findById cuando el ID del usuario no es válido")
    void findById_WithInvalidId() {
        Long invalidId = 0L;  // o -1L

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userController.findById(invalidId, model);
        });

        verify(userService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("findById cuando el ID del usuario no es numérico")
    void findById_WithNonNumericId() {
        assertThrows(NumberFormatException.class, () -> {
            userController.findById(Long.parseLong("abc"), model);
        });

        verify(userService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("findById cuando el ID del usuario es null")
    void findById_WithNullId() {

        assertThrows(NoSuchElementException.class, () -> {
            userController.findById(null, model);
        });

        verify(userService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    /*
     * Method: getFormToCreate(Model)
     */

    @Test
    @DisplayName("getFormToCreate cuando se crea el usuario")
    void getFormToCreate() {

        String view = userController.getFormToCreateNewUser(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("user/form", view);
    }

    /*
     * Method: getFormToCreateNew(Model, Long)
     */

    @Test
    @DisplayName("getFormToCreateNew cuando el ID del usuario no existe")
    void getFormToCreateNew_WhenListDoesNotExist() {
        Long userId = 1L;

        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> {
            userController.getFormToCreateNewUser(model);
        });

        verify(userService).existsById(userId);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID del usuario no es válido")
    void getFormToCreateNew_WithInvalidListId() {
        Long invalidUserId = 0L;

        assertThrows(NoSuchElementException.class, () -> {
            userController.getFormToCreateNewUser(model);
        });

        verify(userService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID del usuario no es numérico")
    void getFormToCreateNew_WithNonNumericId() {
        assertThrows(NumberFormatException.class, () -> {
            userController.getFormToCreateNewUser(model);
        });

        verify(userService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("getFormToCreateNew cuando el ID de usuario es null")
    void getFormToCreateNew_WithNullListId() {

        assertThrows(NoSuchElementException.class, () -> {
            userController.getFormToCreateNewUser(model);
        });

        verify(userService, never()).existsById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario Sí existe")
    void getFormToUpdate_WhenUserExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario NO existe")
    void getFormToUpdate_WhenUserDoesNotExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario no es válido")
    void getFormToUpdate_WithInvalidUserId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdate cunado el ID del usuario no es numérico")
    void getFormToUpdate_WithNonNumericId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario es nulo")
    void getFormToUpdate_WithNullUserId() {
        //
    }

    /*
     * Method: getFormToUpdateAndList(Model, Long, Long)
     */

    /*@Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario Sí existe")
    void getFormToUpdateAndList_WhenUserExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario NO existe")
    void getFormToUpdateAndList_WhenUserDoesNotExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario no es válido")
    void getFormToUpdateAndList_WithInvalidUserId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cunado el ID del usuario no es numérico")
    void getFormToUpdateAndList_WithNonNumericId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario es nulo")
    void getFormToUpdateAndList_WithNullUserId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario Sí existe")
    void getFormToUpdateAndList_WhenUserListExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cuando el ID del usuario NO existe")
    void getFormToUpdateAndList_WhenUserListDoesNotExist() {
        //
    }

    @Test
    @DisplayName("getFormToUpdateAndList cunado el ID del usuario no es numérico")
    void getFormToUpdateAndList_WithNonNumericListId() {
        //
    }*/

    /*
     * Method: save(User, Long)
     */

    @Test
    @DisplayName("save cuando el usuario Sí existe")
    void save_WhenUserExist() {
        //
    }

    @Test
    @DisplayName("save cuando el usuario NO existe")
    void save_WhenUserDoesNotExist() {
        //
    }

    @Test
    @DisplayName("save cuando el usuario no es válido")
    void save_WithInvalidUserId() {
        //
    }

    @Test
    @DisplayName("save cuando el usuario es nulo")
    void save_WithNullUserId() {
        //
    }

    @Test
    @DisplayName("save cuando el ID del usuario Sí existe")
    void save_WhenUserListExist() {
        //
    }

    @Test
    @DisplayName("save cuando el ID del usuario NO existe")
    void save_WhenUserListDoesNotExist() {
        //
    }

    @Test
    @DisplayName("save cunado el ID del usuario no es numérico")
    void save_WithNonNumericListId() {
        //
    }

    /*
     * Method: deleteById(Long)
     */

    @Test
    @DisplayName("deleteById cuando el ID del usuario Sí existe")
    void deleteById_WhenUserExist() {
        Long userId = 1L;

        when(userService.existsById(userId)).thenReturn(true);

        String view = userController.deleteUser(userId);

        verify(userService).existsById(userId);
        verify(userService).deleteUserWithDependencies(userId);
        assertEquals("redirect:/users", view);
    }

    @Test
    @DisplayName("deleteById cuando el ID del usuario NO existe")
    void deleteById_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> {
            userController.deleteUser(userId);
        });

        verify(userService).existsById(userId);
        verify(userService, never()).deleteUserWithDependencies(anyLong());
    }

    @Test
    @DisplayName("deleteById cuando el ID del usuario no es válido")
    void deleteById_WithInvalidUserId() {
        Long invalidId = 0L;

        assertThrows(NoSuchElementException.class, () -> {
            userController.deleteUser(invalidId);
        });

        verify(userService, never()).existsById(anyLong());
        verify(userService, never()).deleteUserWithDependencies(anyLong());
    }

    @Test
    @DisplayName("deleteById cunado el ID del usuario no es numérico")
    void deleteById_WithNonNumericId() {

        assertThrows(NumberFormatException.class, () -> {
            userController.deleteUser(Long.parseLong("abc"));
        });

        verify(userService, never()).existsById(anyLong());
        verify(userService, never()).deleteUserWithDependencies(anyLong());
    }

    @Test
    @DisplayName("deleteById cuando el ID del usuario es nulo")
    void deleteById_WithNullUserId() {

        assertThrows(NoSuchElementException.class, () -> {
            userController.deleteUser(null);
        });

        verify(model, never()).addAttribute(anyString(), any());
    }

    /*
     * Method: deleteAll(Model)
     */

    @Test
    @DisplayName("getFormToUpdate cuando SÍ se han borrado los usuarios o si hay 0 (cero) usuarios")
    void deleteAll_WhenUsersExists() {

        when(userService.count()).thenReturn(0L);

        String view = userController.deleteAllUsers();

        verify(userService).deleteAllUsers();
        assertEquals(0L, userService.count());
        assertEquals("redirect:/users", view);
    }

    @Test
    @DisplayName("getFormToUpdate cuando NO se han borrado los usuarios")
    void deleteAll_WhenUsersDoesNotExist() {

        when(userService.count()).thenReturn(5L); // ?? ¿Cómo probar que realmente se borra?

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userController.deleteAllUsers();
        });

        verify(userService).deleteAllUsers();
        assertNotEquals(0L, userService.count());
        assertEquals(ErrMsg.NOT_DELETED, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando se produce una excepción en el servicio")
    void deleteAll_ServiceThrowsException() {

        doThrow(new RuntimeException("Service error")).when(userService).deleteAllUsers(); // ??

        assertThrows(RuntimeException.class, () -> {
            userController.deleteAllUsers();
        });
    }


}
