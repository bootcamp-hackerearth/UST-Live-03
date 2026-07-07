package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Unit findByIdentifier(String identifier);

    Unit findByIdentifierAndDeletedFalse (String identifier);

    Page<Unit> findAllByDeletedFalse (Pageable pageable);

    List<Unit> findByStatusTrueAndDeletedFalse();

    Page<Unit> findAll(Specification <Unit> example, Pageable pageable);

}
