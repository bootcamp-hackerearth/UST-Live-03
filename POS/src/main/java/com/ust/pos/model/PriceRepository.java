package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifier(String identifier);

    Price deleteByIdentifier(String identifier);

    Price findByProductAndPriceType(String product, String priceType);

}


