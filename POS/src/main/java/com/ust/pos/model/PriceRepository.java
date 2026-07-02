package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {
    Price findByIdentifier(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    List<Price> findByStatusTrue();

    Page<Price> findAll(Specification spec, Pageable pageable);
}
