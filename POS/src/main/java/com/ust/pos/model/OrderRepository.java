package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Orders findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Orders> findAll(Specification<Orders> example, Pageable pageable);
}