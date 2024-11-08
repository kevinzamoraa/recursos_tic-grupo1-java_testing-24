package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.repository.RatingRepo;

import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
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
public class RatingController {

    @Autowired
    private RatingRepo ratingRepository;

    // TODO UserRepo y ResourceRepo usados temporalmente
    @Autowired
    private UserRepo userRepository;
    @Autowired
    private ResourceRepo resourceRepository;

    // http://localhost:8082/ratings
    @GetMapping("ratings")
    public String findAll(Model model) {
        model.addAttribute("ratings", ratingRepository.findAll());
        return "rating/list";
    }

    // http://localhost:8082/ratings/1
    @GetMapping("ratings/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<Rating> ratingOptional = ratingRepository.findById(id);
        ratingOptional.ifPresent(rating -> {
            model.addAttribute("rating", rating);
        });
        return "rating/detail";
    }

    @GetMapping("ratings2/{id}")
    public String findById2(@PathVariable Long id, Model model) {
        return ratingRepository.findById(id)
                .map(rating -> {
                    model.addAttribute("rating", rating);
                    return "rating-detail";
                })
                .orElseGet(() -> {
                    model.addAttribute("message", "Rating no encontrado");
                    return "error";
                });
    }

    // http://localhost:8082/ratings/create
    @GetMapping("ratings/create")
    public String getFormToCreateNewRating(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating/form";
    }

    // http://localhost:8082/ratings/update/1
    @GetMapping("ratings/update/{id}")
    public String getFormToEditRating(Model model, @PathVariable Long id) {
        ratingRepository.findById(id)
                .ifPresent(rating -> model.addAttribute("rating", rating));
        return "rating/form";
    }

    @PostMapping("ratings")
    public String saveRating(@ModelAttribute Rating rating) {
        boolean exists = false;
        if (rating.getId() != null) {
            exists = ratingRepository.existsById(rating.getId());
        }

        // TODO Hacer que el rating tenga un user y un resource. Mientras tanto...
        rating.setUser(userRepository.findById(1L).get());
        rating.setResource(resourceRepository.findById(1L).get());

        if (! exists) {
            ratingRepository.save(rating);
        } else {
            ratingRepository.findById(rating.getId()).ifPresent(ratingDB -> {
                BeanUtils.copyProperties(rating, ratingDB);
                ratingRepository.save(ratingDB);
            });
        }

        return "redirect:/ratings";
    }

    // METODO BORRAR
    // http://localhost:8082/ratings/delete/1
    @GetMapping("ratings/delete/{id}")
    public String deleteRating(@PathVariable Long id) {
        try {
            ratingRepository.deleteById(id);
            return "redirect:/ratings";
        } catch (Exception e) {
            e.printStackTrace(); // Utilizar log.error
            return "error";
        }
    }

}
