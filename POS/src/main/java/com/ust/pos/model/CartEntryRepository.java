package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartEntryRepository extends JpaRepository<CartEntry , Long> {

    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CartEntry> findByCartId(String cartId);

    List<CartEntry> findAllByCartId(String cartId);

    void deleteAllByCartId(String cartId);
}
