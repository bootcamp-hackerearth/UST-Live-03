package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
