package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByIdentifier(String identifier);

    Page<Price> findAllByDeletedFalse(Pageable pageable);

    Price findByIdentifierAndDeletedFalse(String identifier);
}