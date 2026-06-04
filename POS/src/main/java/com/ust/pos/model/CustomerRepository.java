package com.ust.pos.model;


import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdentifier(String identifier);


    void deleteByIdentifier(String identifier);

    Customer findByPhoneNo(String phoneNo);

}
