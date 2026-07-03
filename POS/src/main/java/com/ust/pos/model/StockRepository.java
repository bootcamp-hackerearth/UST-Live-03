package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByIdentifier(String identifier);

    List<Stock> findAllByStatusAndDeletedFalse(Boolean status);

    Page<Stock> findByDeletedFalse(Pageable pageable);

    Page<Stock> findAll(Specification<Stock> example, Pageable pageable);
}
