package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findFirstByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}