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

    void deleteByIdentifier(String identifier);

    List<Price> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Price> findByIsDeleted(boolean isDeleted, Pageable pageable);

    Price findByProductAndPriceType(String productIdentifier, String priceType);

    Page<Price> findAll(Specification<Price> example, Pageable pageable);
}