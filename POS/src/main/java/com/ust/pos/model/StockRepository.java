package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {

    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);

    Page<Stock> findAll(Pageable pageable);

    Page<Stock> findAll(Specification<Stock> spec, Pageable pageable);

    Page<Stock> findByDeletedFalse(Pageable pageable);
}