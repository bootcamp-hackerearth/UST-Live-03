package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdentifier(String identifier);

    Product findByIdentifierAndDeletedFalse(String identifier);

    Page<Product> findByDeletedFalse(Pageable pageable);

    List<Product> findByStatusAndDeletedFalse(boolean status);

    Page<Product> findAll(Specification<Product> example, Pageable pageable);
}