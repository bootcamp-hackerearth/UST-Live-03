package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Customer> findAllByStatus(boolean status);
}
