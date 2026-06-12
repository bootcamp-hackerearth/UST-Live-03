package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {

    CartEntry findByIdentifier(String productId);

    void deleteByIdentifier(String identifier);

    List<CartEntry> findByCartId(String cartId);

    void deleteByCartId(String cartId);
}
