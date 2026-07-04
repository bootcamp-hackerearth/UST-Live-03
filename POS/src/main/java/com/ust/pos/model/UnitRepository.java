package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {

    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Unit> findByStatusIsTrue();

    Page<Unit> findAll(Pageable pageable);

    Page<Unit> findAll(Specification<Unit> spec, Pageable pageable);

    Page<Unit> findByDeletedFalse(Pageable pageable);
}