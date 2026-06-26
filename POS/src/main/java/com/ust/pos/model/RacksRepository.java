package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String identifier);

    Racks findByIdentifierAndDeletedFalse(String identifier);

    List<Racks> findByDeletedFalse();

    Page<Racks> findByDeletedFalse(Pageable pageable);

    Page<Racks> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String identifier, Pageable pageable);
}