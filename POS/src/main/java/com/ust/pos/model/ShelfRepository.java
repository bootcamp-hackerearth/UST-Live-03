package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Shelf findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Shelf> findByIsDeletedFalse(Pageable pageable);
}
