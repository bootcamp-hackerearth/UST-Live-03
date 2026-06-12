package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface CartRepository extends JpaRepository<Cart,Long>{

    Cart findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);
}
