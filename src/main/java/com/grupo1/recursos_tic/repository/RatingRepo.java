package com.grupo1.recursos_tic.repository;

import com.grupo1.recursos_tic.model.Rating;

import com.grupo1.recursos_tic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface RatingRepo extends JpaRepository<Rating, Long> {

    @Query("select r from Rating r where r.user_id = :userId")
    List<Rating> findAllByUserId(Long userId);

}

