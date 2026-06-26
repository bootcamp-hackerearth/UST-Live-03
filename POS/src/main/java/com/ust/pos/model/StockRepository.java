package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse(String identifier);

    Page<Stock> findByDeletedFalse(Pageable pageable);
}