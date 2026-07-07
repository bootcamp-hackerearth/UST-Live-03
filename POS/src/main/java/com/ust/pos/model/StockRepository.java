package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Stock> findByDeletedFalse();

    Page<Stock> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String search, Pageable pageable);

    Page<Stock> findByDeletedFalse(Pageable pageable);

}
