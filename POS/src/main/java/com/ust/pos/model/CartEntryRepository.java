package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry,Long > {
    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CartEntry> findByCartId(String cart);

    Page<CartEntry> findByProductContainingIgnoreCase(String product, Pageable pageable);

}
