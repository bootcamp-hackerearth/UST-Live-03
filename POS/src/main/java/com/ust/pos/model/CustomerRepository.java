package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifierAndDeletedFalse(String identifier);

    Page<Customer> findAllByDeletedFalse(Pageable page);

    Customer findByIdentifier(String identifier);

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);

}
