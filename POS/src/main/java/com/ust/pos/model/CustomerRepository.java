package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifier(String identifier);

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);

    void deleteByIdentifier(String identifier);

    List<Customer> findByStatus(boolean status);

    Page<Customer> findByIsDeletedFalse(Pageable pageable);

}
