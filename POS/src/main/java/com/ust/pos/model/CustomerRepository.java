package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Object findAllByStatus(boolean b);

}
