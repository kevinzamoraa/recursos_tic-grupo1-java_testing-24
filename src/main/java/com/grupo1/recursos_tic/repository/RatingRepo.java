package com.grupo1.recursos_tic.repository;

import com.grupo1.recursos_tic.model.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RatingRepo extends JpaRepository<Rating, Long> {

    List<Rating> findAllByUserId(@PathVariable Long userId);

}

