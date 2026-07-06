package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifier(String identifier);

    Page<Customer> findByDeletedFalse(Pageable pageable);

    List<Customer> findByStatusIsTrueAndDeletedFalse();

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);

}