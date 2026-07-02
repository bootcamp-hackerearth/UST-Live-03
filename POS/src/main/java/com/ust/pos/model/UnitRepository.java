package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UnitRepository  extends JpaRepository<Unit,Long>, JpaSpecificationExecutor<Unit> {
    Unit findByIdentifier(String identifier);

    Page<Unit> findByDeletedFalse(Pageable pageable);

    List<Unit> findByStatusTrue();

    Page<Unit> findAll(Specification spec, Pageable pageable);
}
