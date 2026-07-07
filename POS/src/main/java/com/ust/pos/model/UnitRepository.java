package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    Unit findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Unit> findByStatusIsTrueAndDeletedFalse();

    Page<Unit> findByDeletedFalse(Pageable pageable);

    Page<Unit> findAll(Specification<Unit> example, Pageable pageable);

}
