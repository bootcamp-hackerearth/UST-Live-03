package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Racks> findByDeletedFalse(Pageable pageable);

}
