package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdentifierAndDeletedFalse(String orderIdentifier);

    List<OrderItem> findByDeletedFalse(Pageable pageable);

    OrderItem findByIdentifierAndDeletedFalse(String identifier);

    List<OrderItem> findByDeletedFalse();

    Page<OrderItem> findByIdentifierContainingIgnoreCaseAndDeletedFalse(Pageable pageable, String search);

}
