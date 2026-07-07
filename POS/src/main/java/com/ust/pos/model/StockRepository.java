package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Stock findByIdentifierAndDeletedFalse(String identifier);

    List<Stock> findByDeletedFalse();

    Page<Stock> findByDeletedFalse(Pageable pageable);

    Page<Stock> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String identifier, Pageable pageable);
}