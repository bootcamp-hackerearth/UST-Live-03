package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Customer> findAll(Pageable pageable);

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);
}