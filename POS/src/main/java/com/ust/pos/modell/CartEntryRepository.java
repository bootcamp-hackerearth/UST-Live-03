package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {
    CartEntry findByIdentifier(String identifier);

    CartEntry findByIdentifierAndDeletedFalse(String identifier);

    Page<CartEntry> findAllByDeletedFalse(Pageable pageable);

    List<CartEntry> findByCartIdentifierAndDeletedFalse(String cartIdentifier);
}