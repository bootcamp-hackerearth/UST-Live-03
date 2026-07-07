package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    List<Rack> findByStatusTrueAndDeletedFalse();

    Rack findByIdentifierAndDeletedFalse (String identifier);

    Page<Rack> findAllByDeletedFalse (Pageable pageable);

    Page<Rack> findAll(Specification example, Pageable pageable);

}