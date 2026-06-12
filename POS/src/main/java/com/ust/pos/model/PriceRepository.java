package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price,Long> {

    Price findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Price> findByStatusIsTrue();

}