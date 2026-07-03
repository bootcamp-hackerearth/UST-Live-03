package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Product> findAllByStatus(boolean status);

    Page<Product> findByDeletedFalse(Pageable pageable);

    Page<Product> findAll(Specification<Product> example, Pageable pageable);

}
