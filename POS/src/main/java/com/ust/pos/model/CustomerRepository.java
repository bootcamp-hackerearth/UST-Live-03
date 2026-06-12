package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhoneNo(String phoneNo);

    Customer findById(String identifier);

    void deleteByPhoneNo(String phoneNo);

    List<Customer> findByStatusIsTrue();

}