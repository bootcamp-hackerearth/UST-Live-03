package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfsRepository extends JpaRepository<Shelfs, Long> {
    Shelfs findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Shelfs> findByStatusTrue();

    Page<Shelfs> findByIsDeletedFalse(Pageable pageable);

    List<Shelfs> findByStatusTrueAndIsDeletedFalse();

    Page<Shelfs> findAll(Specification example, Pageable pageable);
}
