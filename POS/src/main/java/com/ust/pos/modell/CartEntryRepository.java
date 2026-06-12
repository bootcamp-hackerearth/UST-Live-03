package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartEntryRepository extends JpaRepository<CartEntry,Long> {
    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CartEntry> findByCartIdentifier(String cartIdentifier);

    void deleteByCartIdentifier(String cartIdentifier);
}