package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.RatingRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepo ratingRepository;

    @Transactional
    public void deleteRatingsByUser(User user) {
        try {
            // ratingRepository.findAllByUserId(userId);
            int deletedCount = ratingRepository.deleteRatingByUserId(user.getId());
            System.out.println("Se han eliminado " + deletedCount + " ratings.");
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar ratings", e);
        }
    }
    /* public void deleteRatingsByUser(List<Rating> ratingsToDelete) {
        try {
            List<Rating> managedRatings = ratingsToDelete.stream()
                    .filter(r -> r.getUserId() != null)
                    .map(RatingRepo::findAllByUserId)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(r -> r.getUserId() != null && r.getUserId().getId() == ratingsToDelete.get(0).getUser().getId())
                    .collect(Collectors.toList());


            if (!managedRatings.isEmpty()) {
                ratingRepository.deleteAll(managedRatings);
                System.out.println("Se han eliminado " + managedRatings.size() + " ratings.");
            } else {
                System.out.println("No se encontraron ratings asociados al usuario especificado.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar ratings", e);
        }
    }*/

}
