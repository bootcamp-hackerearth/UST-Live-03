package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String identifier);

    Racks findByIdentifierAndDeletedFalse(String identifier);

    Page<Racks> findAllByDeletedFalse(Pageable pageable);

    List<Racks> findByStatusTrueAndDeletedFalse();
}