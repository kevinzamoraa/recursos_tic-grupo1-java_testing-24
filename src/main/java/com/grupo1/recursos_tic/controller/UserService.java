package com.grupo1.recursos_tic.controller;

import com.grupo1.recursos_tic.model.Rating;
import com.grupo1.recursos_tic.model.User;
import com.grupo1.recursos_tic.repository.ResourceListsRepo;
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
    private ResourceListsRepo resourceListRepository;

    @Autowired
    private RatingRepo ratingRepository;

    public void deleteUserWithRatings(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            try {
                // Eliminar 'ratings' asociados al usuario
                int deletedCount = ratingRepository.deleteRatingByUserId(user.getId());
                    System.out.println("Se han eliminado " + deletedCount + " ratings.");
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar ratings", e);
            }
            try {
                // Eliminar 'resourceList' asociadas al usuario
                resourceListRepository.deleteResourceListByUserId(user.getId());
                System.out.println("Se han eliminado los recursos creados por el usuario a borrar.");
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el usuario", e);
            }
            try {
                // Eliminar el usuario
                userRepository.deleteById(user.getId());
                System.out.println("Se ha eliminado un usuario.");
            } catch (Exception e) {
                throw new RuntimeException("Error al eliminar el usuario", e);
            }
        }
    }

   public List<Rating> findAllByUserId(User user) {
        return ratingRepository.findAllByUserId(user.getId());
   }

}
