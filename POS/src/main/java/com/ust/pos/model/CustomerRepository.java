package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentifier(String identifier);

    Customer findByPhoneNo(String phoneNo);

    List<Customer> findByStatusTrueAndDeletedFalse();

    @Query("""
        SELECT c
        FROM Customer c
        WHERE c.status = true
        AND c.deleted = false
        AND (
            LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR c.phoneNo LIKE CONCAT('%', :query, '%')
        )
    """)
    List<Customer> searchActiveCustomers(@Param("query") String query);

    Page<Customer> findByDeletedFalse(Pageable pageable);
}