package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Product> findByStatusIsTrue();

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Page<Product> findByDeletedFalse(Pageable pageable);
}