package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdentifier(String identifier);

    Customer findByEmailAndIsDeleteFalse(String email);

    Customer findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Customer> findByIsDeleteFalse();

    Page<Customer> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Customer> findByIsDeleteFalse(Pageable pageable);
}
