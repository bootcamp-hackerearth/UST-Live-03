package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RacksRepository extends JpaRepository<Racks, Long>, JpaSpecificationExecutor<Racks> {
    Racks findByIdentifier(String identifier);

    Page<Racks> findByDeletedFalse(Pageable pageable);

    List<Racks> findByStatusTrue();

    Page<Racks> findAll(Specification spec, Pageable pageable);
}
