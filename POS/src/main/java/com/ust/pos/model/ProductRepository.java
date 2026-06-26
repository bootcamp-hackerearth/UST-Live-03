package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    List<Product> findByStatusTrue();

    boolean existsByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.status = true
            AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR p.identifier LIKE CONCAT('%', :query, '%')
            )
            """)
    List<Product> searchActiveProducts(@Param("query") String query);

    Page<Product> findByIsDeletedFalse(Pageable pageable);
}
