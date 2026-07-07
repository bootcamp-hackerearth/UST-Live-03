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

    void deleteByIdentifier(String identifier);

    List<Product> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Product> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Page<Product> findAll(Specification<Product> example, Pageable pageable);
}