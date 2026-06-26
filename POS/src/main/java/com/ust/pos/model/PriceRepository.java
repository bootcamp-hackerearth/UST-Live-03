package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByIdentifier(String identifier);

    Page<Price> findByDeletedFalse(Pageable pageable);

    List<Price> findAllByStatusAndDeletedFalse(boolean status);
}
