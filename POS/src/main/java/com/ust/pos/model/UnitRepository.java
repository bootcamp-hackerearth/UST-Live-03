package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Unit> findByDeletedFalse(Pageable pageable);

    Page<Unit> findAll(Specification <Unit>example, Pageable pageable);
}