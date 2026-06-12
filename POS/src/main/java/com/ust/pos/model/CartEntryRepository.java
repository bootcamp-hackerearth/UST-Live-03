package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {

    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    void deleteByCartIdentifier(String cartIdentifier);

    Page<CartEntry> findAll(Pageable pageable);

    List<CartEntry> findAllByCartIdentifier(String cartIdentifier);
}