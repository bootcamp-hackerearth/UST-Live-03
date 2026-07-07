package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QueryByExampleExecutor<Customer> {
    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Customer> findAll(Pageable pageable);

    Customer findByEmail(String email);

    Customer findByIdentifierAndDeletedFalse(String identifier);
    Customer findByEmailAndDeletedFalse(String email);
    Page<Customer> findByDeletedFalse(Pageable pageable);
    List<Customer> findByDeletedFalse();
    Page<Customer> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );
}
