package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifier(String identifier);

    @Transactional
    void deleteByIdentifier(String identifier);

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);
}