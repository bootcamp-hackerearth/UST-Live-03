package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Shelf findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Shelf findByIdentifierAndDeletedFalse(String identifier);

    List<Shelf> findByDeletedFalse();

    List<Shelf> findByStatusTrueAndDeletedFalse();

    Page<Shelf> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );

    Page<Shelf> findByDeletedFalse(Pageable pageable);
}
