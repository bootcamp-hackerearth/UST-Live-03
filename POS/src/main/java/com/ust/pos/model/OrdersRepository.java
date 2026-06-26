package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Orders> findByStatus(boolean status);
}
