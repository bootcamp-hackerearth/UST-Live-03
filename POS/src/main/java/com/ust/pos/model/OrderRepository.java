package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order, Long> {
    void deleteByIdentifier(String identifier);

    List<Order> findAllByStatusAndDeletedFalse(boolean status);

    List<Order> findAllByOrderByOrderDateDesc();
}
