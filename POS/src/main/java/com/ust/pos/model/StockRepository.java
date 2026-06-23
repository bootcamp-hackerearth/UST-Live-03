package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Stock> findByStatusIsTrueAndDeletedFalse();

    Page<Stock> findByDeletedFalse(Pageable pageable);
}
