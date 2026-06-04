package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Long> {
    Stocks findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Stocks> findByStatusIsTrue();
}