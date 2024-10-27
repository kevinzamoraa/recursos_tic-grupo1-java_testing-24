package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.repository.RatingRepo;

import com.grupo1.recursos_tic.repository.RatingRepo;
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
public class RatingController {

    private RatingRepo ratingRepository;

    // http://localhost:8080/ratings
    @GetMapping("ratings")
    public String findAll(Model model) {
        model.addAttribute("titulo", "Lista de ratings");
        model.addAttribute("ratings", ratingRepository.findAll());
        return "rating-list";
    }

    // http://localhost:8080/ratings/1
    @GetMapping("ratings/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<Rating> ratingOptional = ratingRepository.findById(id);
        ratingOptional.ifPresent(rating -> model.addAttribute("rating", rating));
        return "rating-detail";
    }
    @GetMapping("ratings2/{id}")
    public String findById2(@PathVariable Long id, Model model) {
        return ratingRepository.findById(id)
                .map(rating -> {
                    model.addAttribute("rating", rating);
                    return "rating-detail";
                })
//                .orElse("error")
                .orElseGet(() -> {
                    model.addAttribute("message", "Rating no encontrado");
                    return "error";
                });
    }

    // http://localhost:8080/ratings/crear
    @GetMapping("ratings/create")
    public String getFormToCreateNewRating(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating-form";
    }

    // http://localhost:8080/ratings/editar/1
    @GetMapping("ratings/update/{id}")
    public String getFormToEditRating(Model model, @PathVariable Long id) {
        ratingRepository.findById(id)
                .ifPresent(rating -> model.addAttribute("rating", rating));
        return "rating-form";
    }

    @PostMapping("ratings")
    public String saveRating(@ModelAttribute Rating rating) {
        boolean exists = false;
        if (rating.getId() != null) {
            exists = ratingRepository.existsById(rating.getId());
        }
        if (! exists) {
            // Crear un nuevo rating
            ratingRepository.save(rating);
        } else {
            // Actualizar un usuario existente
            ratingRepository.findById(rating.getId()).ifPresent(ratingDB -> {
                BeanUtils.copyProperties(rating, ratingDB);
                ratingRepository.save(ratingDB);
            });
        }

        return "redirect:/ratings";
    }

    // METODO BORRAR
    // http://localhost:8080/ratings/borrar/1
    // http://localhost:8080/ratings/borrar/2
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
