package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Warehouse findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Warehouse> findByStatusTrue();

    Page<Warehouse> findByDeletedFalse(Pageable pageable);

    Page<Warehouse> findAll(Specification<Warehouse> spec, Pageable pageable);
}
