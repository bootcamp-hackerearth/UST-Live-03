package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {

    Price findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    Page<Price> findAll(Pageable pageable);

    Page<Price> findAll(Specification<Price> spec, Pageable pageable);

    Page<Price> findByDeletedFalse(Pageable pageable);
}