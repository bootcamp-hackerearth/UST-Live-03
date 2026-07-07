package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse(String identifier);

    Optional<Stock> findByIdAndDeletedFalse(Long id);

    Page<Stock> findAllByDeletedFalse(Pageable pageable);

    Page<Stock> findAll(Specification<Stock> example, Pageable pageable);

}