package com.grupo1.recursos_tic.repository;

import com.grupo1.recursos_tic.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {}
