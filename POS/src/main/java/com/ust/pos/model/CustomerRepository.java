package com.ust.pos.model;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Customer> findByStatusTrue();

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Page<Customer> findAll(Specification spec, Pageable pageable);
}
