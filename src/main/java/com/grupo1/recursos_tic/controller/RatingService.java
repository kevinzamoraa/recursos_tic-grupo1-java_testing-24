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

}
