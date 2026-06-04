package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Warehouse findByIdentifier(String identifier);

    @Modifying
    @Transactional
    void deleteByIdentifier(String identifier);

    List<Warehouse> findByStatusIsTrue();

    Page<Warehouse> findAll(Pageable pageable);
}
