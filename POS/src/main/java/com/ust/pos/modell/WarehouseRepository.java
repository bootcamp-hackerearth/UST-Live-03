package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Warehouse findByIdentifier(String identifier);

    Warehouse findByIdentifierAndDeletedFalse(String identifier);

    Page<Warehouse> findAllByDeletedFalse(Pageable pageable);

    List<Warehouse> findByStatusTrueAndDeletedFalse();

    Page<Warehouse> findAll(Specification<Warehouse> example, Pageable pageable);
}