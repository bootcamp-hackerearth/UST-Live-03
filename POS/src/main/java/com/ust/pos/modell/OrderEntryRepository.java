package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {
    List<OrderEntry> findByOrderIdentifierAndDeletedFalse(String orderIdentifier);
}