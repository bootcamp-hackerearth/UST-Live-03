package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByIdentifier(String identifier);

    Order findByIdentifier(String identifier);

    Order findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Order> findByIsDeleteFalse();

    Page<Order> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Order> findByIsDeleteFalse(Pageable pageable);
}
