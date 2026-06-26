package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfsRepository  extends JpaRepository<Shelfs, Long> {
    Shelfs findByIdentifier(String identifier);

    Page<Shelfs> findByDeletedFalse(Pageable pageable);

    List<Shelfs> findByStatusTrue();
}
