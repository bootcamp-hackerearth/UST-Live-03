package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Shelf findByIdentifier(String identifier);

    Shelf findByIdentifierAndDeletedFalse(String identifier);

    Page<Shelf> findAllByDeletedFalse(Pageable pageable);

    List<Shelf> findByStatusTrueAndDeletedFalse();

    Page<Shelf> findAll(Specification<Shelf> example, Pageable pageable);

}