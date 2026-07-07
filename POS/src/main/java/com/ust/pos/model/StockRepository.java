package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Stock> findByIsDeletedFalse(Pageable pageable);

    Page<Stock> findAll(Specification<Stock> example, Pageable pageable);

    Stock findByIdentifierAndIsDeletedFalse(String identifier);
}
