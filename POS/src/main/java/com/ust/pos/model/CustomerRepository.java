package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdentifier(String identifier);

    Customer findByIdentifierAndDeletedFalse(String identifier);

    Page<Customer> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier,
            Pageable pageable
    );

    Page<Customer> findByDeletedFalse(Pageable pageable);

    List<Customer> findByDeletedFalse();
}
