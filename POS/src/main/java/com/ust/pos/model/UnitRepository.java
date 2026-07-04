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

    Unit deleteByIdentifier(String identifier);

    Page<Unit> findAll(Specification<Unit> example, Pageable pageable);

    List<Unit> findByStatus(boolean status);

    Page<Unit> findByIsDeletedFalse(Pageable pageable);

}
