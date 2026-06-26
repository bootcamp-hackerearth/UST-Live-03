package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}