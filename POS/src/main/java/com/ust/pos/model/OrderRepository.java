package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Orders findByIdentifier(String identifier);

    Orders findByOrderId(String orderId);

    void deleteByIdentifier(String identifier);

    Page<Orders> findAll(Pageable pageable);

    List<Orders> findAllByCustomerIdentifierOrderByOrderDateDesc(String customerIdentifier);
}