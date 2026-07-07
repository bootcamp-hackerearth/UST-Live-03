package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdentifier(String customer);
    Page<Customer> findByIsDeletedFalse(Pageable pageable);
    void deleteByIdentifier(String identifier);
    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);

    Customer findByIdentifierAndIsDeletedFalse(String identifier);
}
