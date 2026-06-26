package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {

    List<OrderEntry> findByOrderIdentifier(String orderIdentifier);

    void deleteByOrderIdentifier(String orderIdentifier);
}
