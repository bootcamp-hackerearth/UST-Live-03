package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {

    void deleteByProductId(String productId);

    void deleteByIdentifier(String identifier);

    Price findByIdentifier(String identifier);
}