package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByIdentifier(String identifier);

    Price findByIdentifierAndDeletedFalse(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    Price findByProductAndPriceType(String product, String priceType);

    Page<Price> findAll(Specification<Price> example, Pageable pageable);
}