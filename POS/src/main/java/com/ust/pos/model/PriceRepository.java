package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifier(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    List<Price> findByStatusTrueAndDeletedFalse();

    @Query("""
        SELECT COUNT(DISTINCT p.priceType)
        FROM Price p
        WHERE p.productId = :productId
        AND p.status = true
        AND p.deleted = false
        AND p.priceType IN ('Selling Price', 'Cost Price', 'MRP')
    """)
    long countActivePriceTypes(@Param("productId") String productId);

    Page<Price> findAll(Specification <Price>example, Pageable pageable);
}