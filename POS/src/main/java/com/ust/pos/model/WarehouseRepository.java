package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    Warehouse findByIdentifier(String identifier);

    @Modifying
    @Transactional
    void deleteByIdentifier(String identifier);

    List<Warehouse> findByStatusIsTrue();

    Page<Warehouse> findByDeletedFalse(Pageable pageable);

    Page<Warehouse> findAll(Pageable pageable);

    Page<Warehouse> findAll(Specification<Warehouse> spec, Pageable pageable);
}