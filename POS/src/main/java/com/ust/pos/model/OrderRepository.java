package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Order> findByIdentifierContainingIgnoreCase(Pageable pageable, String search);

}
