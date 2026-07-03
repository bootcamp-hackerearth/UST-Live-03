package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    void deleteByIdentifier(String identifier);

    Price findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    Page<Price> findAll(Specification<Price> spec, Pageable pageable);
}