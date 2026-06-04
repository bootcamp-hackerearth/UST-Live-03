package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    List<Product> findByStatusTrue();

    boolean existsByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
