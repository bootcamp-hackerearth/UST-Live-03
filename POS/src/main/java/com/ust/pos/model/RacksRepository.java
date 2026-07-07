package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RacksRepository extends JpaRepository<Racks, Long> {
    Racks findByIdentifier(String racks);

    void deleteByIdentifier(String identifier);

    List<Racks> findByStatus(boolean status);

    Page<Racks> findByIsDeletedFalse(Pageable pageable);

    Page<Racks> findAll(Specification<Racks> example, Pageable pageable);

    Racks findByIdentifierAndIsDeletedFalse(String identifier);
}
