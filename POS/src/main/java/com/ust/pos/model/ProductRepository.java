package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Product findByIdentifier(String identifier);

    List<Product> findByStatusTrue();

    Page<Product> findByDeletedFalse(Pageable pageable);

    Page<Product> findAll(Specification spec, Pageable pageable);
}
