package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByIdentifierAndDeletedFalse(String identifier);

    Page<Order> findAllByDeletedFalse(Pageable pageable);

    Page<Order> findAll(Specification<Order> example, Pageable pageable);

}