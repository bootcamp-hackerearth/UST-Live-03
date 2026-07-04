package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RackRepository extends JpaRepository<Rack, Long>, JpaSpecificationExecutor<Rack> {

    Optional<Rack> findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    Page<Rack> findAll(Pageable pageable);

    Page<Rack> findAll(Specification<Rack> spec, Pageable pageable);

    Page<Rack> findByDeletedFalse(Pageable pageable);
}