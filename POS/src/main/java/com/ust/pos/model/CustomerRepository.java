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

    Customer findByPhoneNo(long phoneNo);

    void deleteByIdentifier(String identifier);

    List<Customer> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Customer> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Page<Customer> findAll(Specification<Customer> example, Pageable pageable);
}