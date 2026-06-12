package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

      Price findByProductAndPriceType(String product,String priceType);
}