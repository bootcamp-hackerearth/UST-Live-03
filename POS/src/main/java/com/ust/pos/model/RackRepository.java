package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    Rack findByIdentifierAndDeletedFalse(String identifier);

    List<Rack> findByStatusTrueAndDeletedFalse();

    Page<Rack> findAllByDeletedFalse(Pageable pageable);
}
