package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Stock> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Stock> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Page<Stock> findAll(Specification<Stock> example, Pageable pageable);
}