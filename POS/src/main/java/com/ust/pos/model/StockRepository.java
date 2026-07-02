package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Shelfs> {
    Stock findByIdentifier(String identifier);

    Page<Stock> findByDeletedFalse(Pageable pageable);

    List<Stock> findByStatusTrue();

    Page<Stock> findAll(Specification spec, Pageable pageable);
}
