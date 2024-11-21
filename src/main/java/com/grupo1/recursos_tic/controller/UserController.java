package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.UserRepo;

import com.grupo1.recursos_tic.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserService userService;

    // http://localhost:8082/users
    @GetMapping("users")
    public String findAll(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    // http://localhost:8082/users/1
    @GetMapping("users/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> model.addAttribute("user", user));
        // TODO Gestionar el caso en el que no se encuentra el user o usar el método 2
        return "user/detail";
    }

    // TODO Revisar la incorporación de este método
    @GetMapping("users2/{id}")
    public String findById2(@PathVariable Long id, Model model) {
        return userRepository.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user-detail";
                })
//                .orElse("error")
                .orElseGet(() -> {
                // TODO Lanzar una excepción
                    model.addAttribute("message", "User no encontrado");
                    return "error";
                });
    }

    // http://localhost:8082/users/create
    @GetMapping("users/create")
    public String getFormToCreateNewUser(Model model) {
        model.addAttribute("user", new User());
        return "user/form";
    }

    // http://localhost:8082/users/update/1
    @GetMapping("users/update/{id}")
    public String getFormToEditUser(Model model, @PathVariable Long id) {
        userRepository.findById(id)
                .ifPresent(user -> model.addAttribute("user", user));
        return "user/form";
    }

    @PostMapping("users")
    public String saveUser(@ModelAttribute User user) {
        boolean exists = false;
        if (user.getId() != null) {
            exists = userRepository.existsById(user.getId());
        }
        if (! exists) {
            // Crear un nuevo user
            userRepository.save(user);
        } else {
            // Actualizar un usuario existente
            userRepository.findById(user.getId()).ifPresent(userDB -> {
                BeanUtils.copyProperties(user, userDB);
                userRepository.save(userDB);
            });
        }

        return "redirect:/users/" + user.getId();
    }

    // METODO BORRAR
    // http://localhost:8082/users/delete/1
    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserWithRatings(id);
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // TODO Hacer un método "users/delete" que borre todos los usuarios
    // http://localhost:8082/users/delete/all
    @GetMapping("users/delete/all")
    public String deleteAllUsers() {
        try {
            userService.deleteAllUsers();
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}
