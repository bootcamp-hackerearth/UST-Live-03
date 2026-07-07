package com.ust.pos.models;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByIdentifier(String identifier);


}
