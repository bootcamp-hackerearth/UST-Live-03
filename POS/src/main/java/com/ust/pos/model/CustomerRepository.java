package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Customer> findByStatusIsTrueAndDeletedFalse();

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Customer findByIdentifierAndDeletedFalse(String identifier);
}