package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Cart> findByStatusIsTrue();

    boolean existsByIdentifier(String identifier);
}