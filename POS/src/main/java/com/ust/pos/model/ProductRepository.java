package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Product> findByIdentifierContainingIgnoreCase(String search, Pageable pageable);
}
