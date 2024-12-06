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
import org.springframework.http.HttpStatusCode;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.grupo1.recursos_tic.util.Utility.invalidIntPosNumber;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {


    @Mock
    private UserService userService;
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private UserController userController;
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
        User user1 = User.builder().id(userId)
                .name("User 1").build();
        Optional<User> userOpt = Optional.of(user1);

        when(userService.findById(userId)).thenReturn(userOpt);

        String view = userController.findById(userId, model);

        verify(userService).findById(userId);
        verify(model).addAttribute("user", user1);
        assertEquals("user/detail", view);
    }

    @Test
    @DisplayName("findById cuando el usuario NO existe")
    void findById_WhenUserDoesNotExist() {
        Long invalidId = 999L;

        when(userService.findById(any(Long.class))).thenReturn(Optional.empty());


        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userController.findById(invalidId, model);
        });

        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
        verify(userService).findById(anyLong());
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
        assertThrows(NullPointerException.class, () -> {
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
    void getFormToCreate_WhenUserIsCreated() {

        String view = userController.getFormToCreateNewUser(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("user/form", view);
    }

    /*
     * Method: getFormToUpdate(Model, Long)
     */

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario Sí existe")
    void getFormToEdit_WhenUserExist() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        String view = userController.getFormToEditUser(model, 1L);

        assertFalse(invalidIntPosNumber(userId) || userId == 0);
        verify(userService).findById(1L);
        verify(model).addAttribute("user", user);
        assertEquals("user/form", view);
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario NO existe")
    void getFormToUpdate_WhenUserDoesNotExist() {
        long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userController.findById(userId, model));

        assertFalse(invalidIntPosNumber(userId) || userId == 0);
        verify(userService).findById(userId);
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario no es válido")
    void getFormToUpdate_WithInvalidUserId() {
        Long invalidId = 0L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userController.getFormToEditUser(model, invalidId));

        assertTrue(invalidIntPosNumber(invalidId) || invalidId == 0);
        verify(userService, never()).findById(anyLong());
        verify(model, never()).addAttribute(anyString(), any());
        assertEquals(ErrMsg.INVALID_ID, exception.getMessage());//
    }

    /*  @Test
    @DisplayName("getFormToUpdate cunado el ID del usuario no es numérico")
    void getFormToUpdate_WithNonNumericId() {
        //
    }

    @Test
    @DisplayName("getFormToUpdate cuando el ID del usuario es nulo")
    void getFormToUpdate_WithNullUserId() {
        //
    }*/

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
        Long userId = 999L;

        when(userService.existsById(userId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userController.deleteUser(userId);
        });

        verify(userService).existsById(userId);
        verify(userService, never()).deleteUserWithDependencies(anyLong());
        assertEquals(ErrMsg.NOT_FOUND, exception.getMessage());
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
    @DisplayName("deleteAll cuando NO se han borrado los usuarios o no existen")
    void deleteAll_WhenUsersDoesNotDelete() {

        when(userService.count()).thenReturn(5L); // ?? ¿Cómo probar que realmente se borra?

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.deleteAllUsers();
        });

        verify(userService).deleteAllUsers();
        assertNotEquals(0L, userService.count());
        assertEquals(ErrMsg.NOT_DELETED, exception.getMessage());
    }
}
