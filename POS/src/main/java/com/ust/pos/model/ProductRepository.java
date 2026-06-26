package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Product> findByDeletedFalse(Pageable pageable);

    List<Product> findByStatusTrueAndDeletedFalse();

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.status = true
        AND p.deleted = false
        AND (
            LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.identifier) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """)
    List<Product> searchActiveProducts(@Param("query") String query);
}