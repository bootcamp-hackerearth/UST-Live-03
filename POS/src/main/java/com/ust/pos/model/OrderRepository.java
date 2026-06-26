package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByIdentifier(String identifier);

    @Query("""
            SELECT o
            FROM Order o
            WHERE LOWER(o.identifier) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(o.customer) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<Order> searchOrders(@Param("query") String query);
}
