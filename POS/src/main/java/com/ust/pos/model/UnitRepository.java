package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    Page<Unit> findByIsDeletedFalse(Pageable pageable);

    void deleteByIdentifier(String identifier);

    List<Unit> findByStatusTrueAndIsDeletedFalse();

    Page<Unit> findAll(Specification <Unit> specification, Pageable pageable);

    Unit findByIdentifierAndIsDeletedFalse(String identifier);
}