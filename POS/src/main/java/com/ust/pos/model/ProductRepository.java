package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String product);

    void deleteByIdentifier(String identifier);

    Page<Product> findByIsDeletedFalse(Pageable pageable);

    Page<Product> findAll(Specification<Product> example, Pageable pageable);

    @Query("""
    SELECT p FROM Product p
    WHERE p.isDeleted = false
      AND (
           LOWER(p.name) LIKE LOWER(CONCAT(:keyword, '%'))
        OR LOWER(p.identifier) LIKE LOWER(CONCAT(:keyword, '%'))
      )
    """)
    Page<Product> searchByIdentifierOrName(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Product findByIdentifierAndIsDeletedFalse(String identifier);
}
