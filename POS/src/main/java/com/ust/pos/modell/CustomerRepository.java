package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findFirstByPhoneNo(String phoneNo);

    Customer findById(String identifier);

    void deleteByPhoneNo(String phoneNo);

    List<Customer> findByStatusIsTrue();
}