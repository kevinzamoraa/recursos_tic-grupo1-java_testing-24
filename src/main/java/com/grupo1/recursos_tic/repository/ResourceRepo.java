package com.grupo1.recursos_tic.repository;

import com.grupo1.recursos_tic.model.Resource;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepo extends JpaRepository<Resource, Long> {}