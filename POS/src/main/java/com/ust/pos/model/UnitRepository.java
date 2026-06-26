package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    Unit findByIdentifierAndDeletedFalse(String identifier);

    Page<Unit> findByDeletedFalse(Pageable pageable);

    List<Unit> findByStatusAndDeletedFalse(boolean status);
}