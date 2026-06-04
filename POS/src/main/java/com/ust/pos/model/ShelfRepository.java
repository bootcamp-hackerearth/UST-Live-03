package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Optional<Shelf> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    List<Shelf> findByActiveTrue();

    Page<Shelf> findAll(Pageable pageable);
}