package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    Page<Price> findAll(Pageable pageable);
}