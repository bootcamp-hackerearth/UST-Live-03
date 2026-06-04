package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Shelf findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Shelf> findByStatus(boolean status);
}
