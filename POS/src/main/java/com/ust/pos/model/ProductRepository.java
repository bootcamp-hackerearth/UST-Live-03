package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Product> findByStatusIsTrue();

    List<Product> findByStatusTrue();
}
