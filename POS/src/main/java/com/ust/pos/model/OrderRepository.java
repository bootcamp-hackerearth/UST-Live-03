package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdentifier(String identifier);

    boolean existsByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Order> findAll(Specification<Order> example, Pageable pageable);
}
