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
                // System.out.println(user.toString());
            // Eliminar ratings asociados al usuario
            List<Rating> ratingsToDelete = findAllByUserId(user.getId());
                // System.out.println(ratingsToDelete.toString());
            ratingsToDelete.forEach(ratingRepository::delete);

            // Eliminar el usuario
            userRepository.delete(user);
        }
    }

   public List<Rating> findAllByUserId(User user) {
        return ratingRepository.findAllByUserId(user);
   }

}
