package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse (String identifier);

    Page<Stock> findAllByDeletedFalse (Pageable pageable);

    Optional<Stock> findByIdAndDeletedFalse(Long id);

    Page<Stock> findAll(Specification example, Pageable pageable);

}
