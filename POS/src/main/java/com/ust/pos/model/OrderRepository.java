package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Order> findAllByOrderByOrderDateDesc();

    Page<Order> findByDeletedFalse(Pageable pageable);

}