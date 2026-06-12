package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhoneNum(String username);

    void deleteByIdentifier(String identifier);

    Customer findByIdentifier(String identifier);
}
