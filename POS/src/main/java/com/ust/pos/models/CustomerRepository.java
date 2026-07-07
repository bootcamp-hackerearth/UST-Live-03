package com.ust.pos.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhoneNo(String phoneNo);

    Customer findByPhoneNoAndDeletedFalse(String phoneNo);

    Customer findByIdentifierAndDeletedFalse(String identifier);

    Page<Customer> findAllByDeletedFalse(Pageable pageable);

    List<Customer> findByStatusIsTrueAndDeletedFalse();

    Page<Customer> findAll(Specification example, Pageable pageable);
}