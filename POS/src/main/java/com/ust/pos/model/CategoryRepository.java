package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}