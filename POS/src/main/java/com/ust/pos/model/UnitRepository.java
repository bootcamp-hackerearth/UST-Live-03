package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifierAndDeletedFalse(String identifier);

    Page<Unit> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String identifier, Pageable pageable);

    Page<Unit> findByDeletedFalse(Pageable pageable);

    List<Unit> findByDeletedFalse();
}