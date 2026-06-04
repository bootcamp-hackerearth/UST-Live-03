package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
