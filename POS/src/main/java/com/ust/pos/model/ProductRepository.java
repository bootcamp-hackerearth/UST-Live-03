package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    List<Product> findByStatusTrue();

    Page<Product> findByDeletedFalse(Pageable pageable);
}
