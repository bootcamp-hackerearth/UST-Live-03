package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByIdentifier(String identifier);

    Price deleteByIdentifier(String identifier);

    Page<Price> findAll(Specification<Price> example, Pageable pageable);

    Price findByProductAndPriceType(String product, String priceType);

    Page<Price> findByIsDeletedFalse(Pageable pageable);

}


