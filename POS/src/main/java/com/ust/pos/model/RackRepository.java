package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    Page<Rack> findByDeletedFalse(Pageable pageable);

    List<Rack> findByStatusTrueAndDeletedFalse();

    Page<Rack> findAll(Specification <Rack>example, Pageable pageable);
}