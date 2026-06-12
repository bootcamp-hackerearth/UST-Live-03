package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {
    CartEntry findByIdentifier(String identifier);

    List<CartEntry> findByCart(String cart);

    CartEntry deleteByIdentifier(String identifier);

    void deleteByCart(String cart);
}
