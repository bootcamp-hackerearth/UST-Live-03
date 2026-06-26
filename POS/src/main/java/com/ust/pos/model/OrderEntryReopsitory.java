package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEntryReopsitory extends JpaRepository<OrderEntry,Long> {
    OrderEntry findByIdentifier(String identifier);
    void deleteByIdentifier(String identifier);
    List<OrderEntry> findAllByOrderId(String orderId);

}
