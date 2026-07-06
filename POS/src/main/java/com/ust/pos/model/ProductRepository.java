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

    List<Product> findByStatusTrueAndDeletedFalse();

    Page<Product> findAllByDeletedFalse(Pageable pageable);

    Page<Product> findAll(Specification<Product> example, Pageable pageable);
}

