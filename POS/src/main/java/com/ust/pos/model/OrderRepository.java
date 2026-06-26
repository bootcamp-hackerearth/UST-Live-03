package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Order> findAllByCustomer(String customer);
}