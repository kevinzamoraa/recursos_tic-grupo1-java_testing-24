package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.RatingRepo;

import com.grupo1.recursos_tic.repository.ResourceRepo;
import com.grupo1.recursos_tic.repository.UserRepo;
import com.grupo1.recursos_tic.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.grupo1.recursos_tic.util.Utility.*;

@Controller
@AllArgsConstructor
public class RatingController {

    private RatingService ratingService;

    // TODO usar servicio en vez de repositorio

    private RatingRepo ratingRepository;
    private ResourceRepo resourceRepository;
    private UserRepo userRepository;

    private final String idMsg = "Falta el id o no es un entero positivo";
    private final String notIdMsg = "La valoración no existe";
    private final String dataMsg = "Los datos recibidos no son válidos";
    private final String delMsg = "Error al borrar todos los recursos";

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

    @GetMapping("ratings/create/{resourceId}")
    public String getFormToCreateNewRatingWithResourceId(
            @PathVariable Long resourceId, Model model) {
        if (invalidIntPosNumber(resourceId) || resourceId == 0)
            throw new NoSuchElementException(idMsg);

        if (!resourceRepository.existsById(resourceId)
                || !userRepository.existsById(userAuth().get().getId()))
            throw new NoSuchElementException(notIdMsg);

        Rating rating = new Rating();
        rating.setResource(resourceRepository.findById(resourceId).get());
        rating.setUser(userRepository.findById(userAuth().get().getId()).get());
        model.addAttribute("rating", rating);
        return "rating/form";
    }

    // http://localhost:8082/ratings/update/1
    @GetMapping("ratings/update/{id}")
    public String getFormToEditRating(Model model, @PathVariable Long id) {
        if (invalidIntPosNumber(id) || id == 0)
            throw new NoSuchElementException(idMsg);

        if (!ratingRepository.existsById(id))
            throw new NoSuchElementException(notIdMsg);

        Rating rating = ratingRepository.findById(id).get();
        model.addAttribute("rating", rating);

        return "rating/form";
    }

    @PostMapping("ratings")
    public String saveRating(@ModelAttribute Rating rating) {
        if (rating == null) throw new NoSuchElementException(dataMsg);
        // String error = formValidation(rating);
        // if (error != null) throw new NoSuchElementException(error);

        //if (confirmUser(rating.getUser())) {
            if (rating.getId() == null) {
                rating.setCreatedAt(LocalDate.now()); // Fecha de creación
                ratingRepository.save(rating);
            } else {
                ratingRepository.findById(rating.getId()).ifPresent(ratingDB -> {
                    BeanUtils.copyProperties(rating, ratingDB);
                    ratingRepository.save(ratingDB);
                });
            }
        //} else throw new NoSuchElementException(dataMsg);

        return "redirect:/resources/" + rating.getResource().getId();
    }

    // METODO BORRAR
    // http://localhost:8082/ratings/delete/1
    @GetMapping("ratings/delete/{id}")
    public String deleteRating(@PathVariable Long id) {
        try {
            Long resourceId = ratingService.findById(id).get().getResource().getId();
            ratingService.deleteRatingById(id);
            return "redirect:/resources/" + resourceId;
        } catch (Exception e) {
            e.printStackTrace(); // Utilizar log.error
            return "error";
        }
    }

}
