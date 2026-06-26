package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}