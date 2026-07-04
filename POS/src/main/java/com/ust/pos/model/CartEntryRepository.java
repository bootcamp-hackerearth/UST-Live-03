package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {

    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<CartEntry> findAll(Specification<CartEntry> example, Pageable pageable);

    List<CartEntry> findByCartId(String cartId);

    List<CartEntry> findAllByCartId(String cartId);

    void deleteAllByCartId(String cartId);
}
