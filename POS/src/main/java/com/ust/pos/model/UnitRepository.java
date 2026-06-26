package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdentifier(String identifier);

    Unit findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Unit> findByIsDeleteFalse();

    Page<Unit> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Unit> findByIsDeleteFalse(Pageable pageable);
}