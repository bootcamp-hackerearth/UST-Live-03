package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShelfsRepository  extends JpaRepository<Shelfs, Long>, JpaSpecificationExecutor<Shelfs> {
    Shelfs findByIdentifier(String identifier);

    Page<Shelfs> findByDeletedFalse(Pageable pageable);

    List<Shelfs> findByStatusTrue();

    Page<Shelfs> findAll(Specification spec, Pageable pageable);
}
