package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    Unit findByIdentifierAndDeletedFalse(String identifier);

    Page<Unit> findAllByDeletedFalse(Pageable pageable);

    Page<Unit> findAll(Specification<Unit> example, Pageable pageable);
}