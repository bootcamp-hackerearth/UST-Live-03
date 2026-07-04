package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long>, JpaSpecificationExecutor<Shelf> {

    Optional<Shelf> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    List<Shelf> findByActiveTrue();

    Page<Shelf> findAll(Pageable pageable);

    Page<Shelf> findAll(Specification<Shelf> spec, Pageable pageable);

    Page<Shelf> findByDeletedFalse(Pageable pageable);
}