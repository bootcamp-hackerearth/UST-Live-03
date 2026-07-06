package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StocksRepository extends JpaRepository<Stocks, Long> {

    Stocks findByIdentifier(String identifier);

    Page<Stocks> findByDeletedFalse(Pageable pageable);

    List<Stocks> findByStatusIsTrueAndDeletedFalse();

    Page<Stocks> findAll(Specification<Stocks> example, Pageable pageable);

}