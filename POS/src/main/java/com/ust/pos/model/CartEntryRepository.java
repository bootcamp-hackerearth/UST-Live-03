package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public interface CartEntryRepository extends JpaRepository<CartEntry, Long> {

    CartEntry findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CartEntry> findAllByStatus(boolean status);

    List<CartEntry> findByCart(String cart);

}