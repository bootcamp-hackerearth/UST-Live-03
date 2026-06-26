package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse(String identifier);

    List<Stock> findByStatusTrueAndDeletedFalse();

    Page<Stock> findAllByDeletedFalse(Pageable pageable);

    Stock findByProductAndDeletedFalse(String product);
}
