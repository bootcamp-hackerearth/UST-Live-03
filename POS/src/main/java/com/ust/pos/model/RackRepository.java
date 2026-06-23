package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RackRepository extends JpaRepository<Rack, Long> {

    Rack findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Rack> findByDeletedFalse(Pageable pageable);

    List<Rack> findByStatusIsTrueAndDeletedFalse();
}
