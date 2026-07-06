package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByIdentifier(String identifier);

    Price findByIdentifierAndDeletedFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Price> findByStatusIsTrueAndDeletedFalse();

    Page<Price> findByDeletedFalse(Pageable pageable);

    Page<Price> findAll(Specification<Price> example, Pageable pageable);
}