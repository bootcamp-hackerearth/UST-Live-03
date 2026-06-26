package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdentifier(String identifier);

    List<Order> findByCustomer(String customer);

    void deleteByIdentifier(String identifier);
}