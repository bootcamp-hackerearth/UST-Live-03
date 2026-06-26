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

    Page<Stock> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier,
            Pageable pageable
    );

    Page<Stock> findByDeletedFalse(Pageable pageable);

    List<Stock> findByDeletedFalse();
}
