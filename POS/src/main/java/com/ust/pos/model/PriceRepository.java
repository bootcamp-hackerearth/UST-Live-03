package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifierAndDeletedFalse(String identifier);

    Price findByProductIdentifierAndPriceType(String productIdentifier, String priceType);

    Page<Price> findAllByDeletedFalse(Pageable pageable);

    Price findByIdentifier(String identifier);

}
