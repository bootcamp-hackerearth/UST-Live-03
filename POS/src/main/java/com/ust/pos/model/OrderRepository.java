package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Order findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Order> findByIdentifierContainingIgnoreCase(Pageable pageable, String search);
}