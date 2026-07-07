package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifier(String identifier);

    Price findByProductIdentifier(String productIdentifier);

    void deleteByIdentifier(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    List<Price> findByStatusIsTrueAndDeletedFalse();

    Page<Price> findAll(Specification<Price> example, Pageable pageable);

}
