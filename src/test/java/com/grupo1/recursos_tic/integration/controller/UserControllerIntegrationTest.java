package com.grupo1.recursos_tic.integration.controller;

import com.grupo1.recursos_tic.config.WithMockUserSecurityContextFactory;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.model.UserRole;
import com.grupo1.recursos_tic.repository.UserRepo;
import com.grupo1.recursos_tic.service.RatingService;
import com.grupo1.recursos_tic.service.UserService;
import com.grupo1.recursos_tic.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

//    @BeforeEach
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    public void setUp() {
//        // Configurar el contexto de seguridad antes de cada prueba
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                "admin", "Admin1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }

    @BeforeEach
    void setUp() {
        userService.deleteAllUsers();

        User admin = User.builder()
                .email("admin@admin.es")
                .role(UserRole.ADMIN)
                .username("admin")
                .password(passwordEncoder.encode("Admin1234"))
                .build();
        userService.save(admin);

        var auth = new UsernamePasswordAuthenticationToken(
                admin,
                admin.getPassword(),
                admin.getAuthorities()
        );

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }


    @Test
    //@WithMockUser(username = "admin", roles = "ADMIN")
    void test() throws Exception {
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                "admin", "Admin1234", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
//        User user = userRepo.save(User.builder().username("admin").password("Admin1234").role(UserRole.ADMIN).build());

//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

//        SecurityContextHolder.getContext().setAuthentication(authentication);

//        userRepo.save(User.builder().username("user").password("User1234").role(UserRole.READER).build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", hasSize(1)))
        ;
    }

    @Test
    void findAll() throws Exception {
        /* TODO: Enable login config to allow access to user's list. It is redirecting to login view */
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    //            .andExpect(jsonPath("$[0].role").value(is(UserRole.ADMIN)));
    }

    @Test
    void findById() {
    }

    @Test
    void getFormToCreateNewUser() {
    }

    @Test
    void getFormToEditUser() {
    }

    @Test
    void save() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void deleteAllUsers() {
    }
}