package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdentifier(String orderIdentifier);

    OrderItem findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<OrderItem> findByIdentifierContainingIgnoreCase(Pageable pageable, String search);

}
