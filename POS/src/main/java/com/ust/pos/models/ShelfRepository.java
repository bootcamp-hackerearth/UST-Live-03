package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Shelf findByIdentifier(String identifier);

    List<Shelf> findByStatusTrueAndDeletedFalse();

    Shelf findByIdentifierAndDeletedFalse (String identifier);

    Page<Shelf> findAllByDeletedFalse (Pageable pageable);

    Page<Shelf> findAll(Specification <Shelf> example, Pageable pageable);
}
