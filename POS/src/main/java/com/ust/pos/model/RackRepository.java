package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RackRepository extends JpaRepository<Rack, Long> {
    Rack findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Rack> findByIsDeletedFalse(Pageable pageable);

    Page<Rack> findAll(Specification <Rack> specification, Pageable pageable);
}
