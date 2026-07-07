package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Customer findByIdentifier(String identifier);

    Customer findByIdentifierAndDeletedFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Customer> findByDeletedFalse();

    Page<Customer> findByIdentifierContainingIgnoreCaseAndDeletedFalse
            (String identifier, Pageable pageable);

    Page<Customer> findByDeletedFalse(Pageable pageable);

}