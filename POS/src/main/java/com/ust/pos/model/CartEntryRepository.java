package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {

    CartEntry findByIdentifier(String identifier);

    CartEntry deleteByIdentifier(String identifier);

    List<CartEntry> findByCart(String cart);
}
