package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByIdentifier(String identifier);

    Stock findByIdentifierAndDeletedFalse(String identifier);

    Page<Stock> findAllByDeletedFalse(Pageable pageable);

    Stock findByProduct(String product);

    Stock findByProductAndDeletedFalse(String product);

    @Query("SELECT s.product FROM Stock s WHERE s.deleted = false AND s.quantity > 0")
    List<String> findProductIdentifiersWithStock();

}
