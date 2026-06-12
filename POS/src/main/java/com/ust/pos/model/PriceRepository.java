package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifier(String identifier);

    Price findByProductIdentifier(String productIdentifier);

    void deleteByIdentifier(String identifier);

    List<Price> findByStatusIsTrue();

}
