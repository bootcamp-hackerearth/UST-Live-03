package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByStatusTrueAndDeletedFalse();

    Cart findByIdentifier(String identifier);

    Cart findByIdentifierAndDeletedFalse(String identifier);

    Page<Cart> findAllByDeletedFalse(Pageable pageable);

    Page<Cart> findAll(Specification<Cart> example, Pageable pageable);

}