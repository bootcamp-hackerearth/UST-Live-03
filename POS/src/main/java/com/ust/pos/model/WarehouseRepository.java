package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Warehouse findByIdentifier(String identifier);

    Warehouse findByIdentifierAndDeletedFalse(String identifier);

    List<Warehouse> findByStatusTrueAndDeletedFalse();

    Page<Warehouse> findAllByDeletedFalse(Pageable pageable);

    Page<Warehouse> findAll(Specification<Warehouse> example, Pageable pageable);
}
