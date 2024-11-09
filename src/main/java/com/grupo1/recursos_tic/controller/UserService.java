package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.UserRepo;
import com.grupo1.recursos_tic.repository.RatingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private RatingRepo ratingRepository;

    public void deleteUserWithRatings(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                    // System.out.println(user.toString());
                // Eliminar ratings asociados al usuario
                // List<Rating> ratingsToDelete = findAllByUserId(user);
                    // System.out.println(ratingsToDelete.toString());
                int deletedCount = ratingRepository.deleteRatingByUserId(user.getId());
                    System.out.println("Se han eliminado " + deletedCount + " ratings.");
                // ratingsToDelete.forEach(ratingRepository::delete);
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar ratings", e);
            }
            try {
                // Eliminar el usuario
                userRepository.delete(user);
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el usuario", e);
            }
        }
    }

   public List<Rating> findAllByUserId(User user) {
        return ratingRepository.findAllByUserId(user.getId());
   }

}
