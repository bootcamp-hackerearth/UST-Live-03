package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Shelf findByIdentifier(String identifier);

    List<Shelf> findAllByStatusAndDeletedFalse(Boolean status);

    Page<Shelf> findByDeletedFalse(Pageable pageable);

    Page<Shelf> findAll(Specification<Shelf> example, Pageable pageable);
}
