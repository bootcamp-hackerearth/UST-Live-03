package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersEntryRepository extends JpaRepository<OrdersEntry, Long> {

    OrdersEntry findByIdentifier(String identifier);

    List<OrdersEntry> findByOrdersId(String ordersId);

    void deleteByIdentifier(String identifier);

    void deleteByOrdersId(String ordersId);
}