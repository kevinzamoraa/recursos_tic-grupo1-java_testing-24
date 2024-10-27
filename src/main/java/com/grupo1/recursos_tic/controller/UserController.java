package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.UserRepo;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@AllArgsConstructor
@Controller
public class UserController {

    private UserRepo userRepository;

    // http://localhost:8080/users
    @GetMapping("users")
    public String findAll(Model model) {
        model.addAttribute("titulo", "Lista de users");
        model.addAttribute("users", userRepository.findAll());
        return "user-list";
    }

    // http://localhost:8080/users/1
    @GetMapping("users/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> model.addAttribute("user", user));
        return "user-detail";
    }
    @GetMapping("users2/{id}")
    public String findById2(@PathVariable Long id, Model model) {
        return userRepository.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user-detail";
                })
//                .orElse("error")
                .orElseGet(() -> {
                    model.addAttribute("message", "User no encontrado");
                    return "error";
                });
    }

    // http://localhost:8080/users/crear
    @GetMapping("users/create")
    public String getFormToCreateNewUser(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    // http://localhost:8080/users/editar/1
    @GetMapping("users/update/{id}")
    public String getFormToEditUser(Model model, @PathVariable Long id) {
        userRepository.findById(id)
                .ifPresent(user -> model.addAttribute("user", user));
        return "user-form";
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

        return "redirect:/users";
    }

    // METODO BORRAR
    // http://localhost:8080/users/borrar/1
    // http://localhost:8080/users/borrar/2
    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return "redirect:/users";
        } catch (Exception e) {
            e.printStackTrace(); // Utilizar log.error
            return "error";
        }
    }

}
