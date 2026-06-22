package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {

    OrderEntry findByIdentifier(String identifier);

    List<OrderEntry> findAllByOrderIdentifier(String orderIdentifier);

    void deleteByOrderIdentifier(String orderIdentifier);
}