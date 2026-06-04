package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}